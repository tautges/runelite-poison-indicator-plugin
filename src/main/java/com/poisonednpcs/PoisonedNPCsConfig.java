package com.poisonednpcs;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("poisonedNPCs")
public interface PoisonedNPCsConfig extends Config
{

	@ConfigItem(
			keyName = "displayNPCPoisonOverlay",
			name = "Display NPC poison overlay",
			description = "Display a detailed poison overlay with poison status on individual NPCs"
	)
	default boolean displayNPCPoisonOverlay() {
		return false;
	}

	@ConfigItem(
			keyName = "displayNPCOverlayNextDamage",
			name = "Display next poison damage",
			description = "Display what the next poison splat damage within parentheses in the overlay"
	)
	default boolean displayNPCOverlayNextDamage() {
		return false;
	}

	@ConfigItem(
			keyName = "displayNPCOverlayTotalDamage",
			name = "Display poison damage remaining",
			description = "Display the remaining damage the inflicted poison will cause in the overlay"
	)
	default boolean displayNPCOverlayTotalDamage() {
		return false;
	}

	/*@ConfigItem(
			keyName = "testValue",
			name = "Test Value",
			description = "A test value"
	)
	default int testValue() {
		return 40;
	}*/
}
