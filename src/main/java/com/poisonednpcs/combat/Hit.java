package com.poisonednpcs.combat;

import lombok.Getter;
import net.runelite.api.Hitsplat;

import java.time.Instant;
import java.util.Optional;

@Getter
public class Hit {

    private final Optional<Weapon> weapon;
    private final Hitsplat hitsplat;
    private final Instant occurredAt;

    public Hit(Optional<Weapon> weapon, Hitsplat hitsplat) {
        this.weapon = weapon;
        this.hitsplat = hitsplat;
        // TODO: a little sus to just assume that a created hit occurred at the time this object was instantiated
        this.occurredAt = Instant.now();
    }
}
