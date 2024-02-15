package com.poisonednpcs.combat;

import com.poisonednpcs.poison.PoisonType;
import lombok.Getter;

import java.util.Optional;

public class Weapon {

    private final int id;
    @Getter
    private final Optional<PoisonType> poisonType;

    public Weapon(int id, Optional<PoisonType> poisonType) {
        this.id = id;
        this.poisonType = poisonType;
    }
}
