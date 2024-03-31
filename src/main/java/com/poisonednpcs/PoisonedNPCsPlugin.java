package com.poisonednpcs;

import com.google.common.base.Predicates;
import com.google.inject.Provides;
import com.poisonednpcs.combat.Hit;
import com.poisonednpcs.combat.Weapon;
import com.poisonednpcs.health.HitsplatType;
import com.poisonednpcs.health.PoisonWatchTimer;
import com.poisonednpcs.npcs.NPCTrackingService;
import com.poisonednpcs.npcs.NPCUtils;
import com.poisonednpcs.npcs.Opponent;
import com.poisonednpcs.npcs.OpponentCuller;
import com.poisonednpcs.poison.PoisonType;
import com.poisonednpcs.util.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Displays the poison status of NPCs which are interacting with the client's player. If the player has hit an NPC with
 * a poisoned weapon, the plugin will display via an info box the time until the next possible moment at which poison
 * may begin to splat. In the event that the NPC is confirmed to be poisoned (by observing a poison splat), the plugin
 * will instead track the time until the next poison splat, the damage that splat will occur, and the total amount of
 * remaining damage that sequence of poison will cause.
 *
 * Because the application of poison is not indicated by the client or the game, the plugin can only guess and confirm
 * when poison splats will occur. Only by eventually observing those poison splats can applied poison be confirmed.
 *
 * The plugin also includes an optional overlay, hidden by default, which will display the time until the next possible
 * poison or the next poison splat directly over the relevant NPC's head. Other configurable parameters, hidden by
 * default, allow the next poison splat damage and total poison damage remaining to be included in the overlay.
 */
@Slf4j
@PluginDescriptor(
	name = "Poisoned NPCs"
)
public class PoisonedNPCsPlugin extends Plugin {

	/**
	 * The maximum distance to an NPC, in discrete local coordinates, at which the plugin should still observe that NPC.
	 */
	private static final int MAX_RENDER_DISTANCE = 1700;

	@Inject
	private Client client;

	// TODO: try to remove this and see if it will works
	@Inject
	private PoisonedNPCsConfig config;

	@Inject
	private PoisonedNPCsOverlay poisonedNPCsOverlay;

	@Inject
	private NPCTrackingService npcTrackingService;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp() {
		overlayManager.add(poisonedNPCsOverlay);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(poisonedNPCsOverlay);
		infoBoxManager.removeIf(t -> t instanceof PoisonWatchTimer);
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
		Actor actor = hitsplatApplied.getActor();
		if (!(actor instanceof NPC)) {
			log.trace(String.format("seeing hitsplat on a non-npc: %s", actor.getName()));
			return;
		}

		NPC npc = (NPC) actor;
		npcTrackingService.enterCombat(npc);

		Opponent opponent = npcTrackingService.getOpponent(NPCUtils.getIdentifier(npc));
		if (opponent == null) {
			log.error(String.format("no registered combat with opponent: %s", npc.getName()));
			return;
		}

		Hitsplat hitsplat = hitsplatApplied.getHitsplat();

		// we pull this value prior to recording the hit in case of concurrency concerns with the box renderer
		// TODO: wonder if we could accomplish the same thing by making this synchronized
		// TODO: should we actually be checking if the opponent was already in combat prior to this hit?
		boolean isActiveTimer = opponent.getHealthStatus().isActive();

		HitsplatType hitsplatType = HitsplatType.of(hitsplat.getHitsplatType());
		switch (hitsplatType) { // TODO: replace with mapping(?)
			case OPPONENT_DAMAGED_BY_ME:
				opponent.getHealthStatus().getHitTracker().trackHit(new Hit(Optional.of(getWieldedWeapon()), hitsplat));
				break;
			case OPPONENT_DAMAGED_BY_POISON:
				opponent.getHealthStatus().getPoisonTracker().registerPoisonSplat(new Hit(Optional.empty(), hitsplat));
				break;
			default:
				// Otherwise we don't care about this hitsplat
				return;
		}

		// if a timer wasn't active before but should be now, set one up now that we're interested
		if (!isActiveTimer && opponent.getHealthStatus().isActive()) {
			PoisonWatchTimer poisonWatchTimer = new PoisonWatchTimer(opponent, itemManager.getImage(ItemID.POISON), this);
			infoBoxManager.addInfoBox(poisonWatchTimer);
		}
		// Hacky way to update the timer tooltip to be informative only on updated hits. This predicate will always
		// return false. It is designed to find the correct timer amid all info boxes and update its tooltip only. The
		// alternative to this is to keep references to timers mapped to opponents, which would interfere with culling
		// timers by the timers' own judgment at the correct times (since garbage collection wouldn't sweep up).
		infoBoxManager.removeIf(PoisonWatchTimer.updateTooltip(opponent));
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		// On every game tick, we cull from our combat tracker any NPC's which have become irrelevant.

		final LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();

		OpponentCuller culler = OpponentCuller.newBuilder()
			.add(opponent -> opponent.getNPC().isDead())
			// cull any opponents that are not loaded in the current scene
			.add(opponent -> !opponent.getNPC().getLocalLocation().isInScene())
			// cull any opponents that are too far away on the screen (we wouldn't see their hitsplats anyway)
			// TODO: this last condition isn't perfect; we can still have issues; can't tell when the hitsplats are visible
			.add(opponent -> playerLocation.distanceTo(opponent.getNPC().getLocalLocation()) > MAX_RENDER_DISTANCE)
			// poison wears off the instant the NPC stops interacting with the player
			.add(opponent -> !client.getLocalPlayer().equals(opponent.getNPC().getInteracting()))
			.build();

		// cull from both the overlay (through the tracking service) and from the info boxes
		npcTrackingService.cullIf(culler);
		infoBoxManager.removeIf(PoisonWatchTimer.cullOpponents(culler));
	}

	@Provides
	PoisonedNPCsConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PoisonedNPCsConfig.class);
	}

	/** Retrieves a {@link Weapon} representation of the weapon currently wielded by the client's local player. */
	private Weapon getWieldedWeapon() {
		int weaponId = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);
		ItemComposition weaponComposition = itemManager.getItemComposition(weaponId);

		// Both here and below, we determine the poison level of the weapon by regex'ing against the weapon's name
		for (PoisonType type : ArrayUtils.filter(PoisonType.values(), PoisonType::isMelee)) {
			if (type.getWeaponRegex().matcher(weaponComposition.getName()).find()) {
				return new Weapon(weaponId, Optional.of(type));
			}
		}

		ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);
		Item[] items = itemContainer != null ? itemContainer.getItems() : new Item[0];

		if (items.length > EquipmentInventorySlot.AMMO.getSlotIdx()) {
			final Item ammo = items[EquipmentInventorySlot.AMMO.getSlotIdx()];
			final ItemComposition ammoComposition = itemManager.getItemComposition(ammo.getId());
			for (PoisonType type : ArrayUtils.filter(PoisonType.values(), Predicates.not(PoisonType::isMelee))) {
				if (type.getWeaponRegex().matcher(ammoComposition.getName()).find()) {
					return new Weapon(weaponId, Optional.of(type));
				}
			}
		}

		// We didn't find poison, so treat the weapon as unpoisoned
		return new Weapon(weaponId, Optional.empty());
	}
}
