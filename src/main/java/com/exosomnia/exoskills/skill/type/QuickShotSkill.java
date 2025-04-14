package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class QuickShotSkill extends BaseSkill {
    public QuickShotSkill(int id) {
        super(id);
    }

    public int cooldownForRank(byte rank) {
        return switch (rank) {
            case 0 -> 20;
            case 1, 2 -> 10;
            default -> 5;
        };
    }

    public int shotsForRank(byte rank) {
        //This represents the amplifier of the effect, which is 0-based
        return switch (rank) {
            case 0, 1 -> 0;
            case 2 -> 1;
            default -> 9;
        };
    }
}
