package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class EnchantedBobberSkill extends BaseSkill {
    public EnchantedBobberSkill(int id) {
        super(id);
    }

    public int getMaxHealth(byte rank) {
        return switch (rank) {
            case 0 -> 3;
            case 1 -> 5;
            default -> 10;
        };
    }

    public int getLuckDuration(byte rank) {
        return switch (rank) {
            case 0 -> 1800;
            case 1 -> 3600;
            default -> 10;
        };
    }
}
