package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class PreciseStrikesSkill extends BaseSkill {
    public PreciseStrikesSkill(int id) {
        super(id);
    }

    public int amountForRank(byte rank) {
        return switch (rank) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            default -> 9;
        };
    }

    public int durationForRank(byte rank) {
        return switch (rank) {
            case 0 -> 8;
            case 1 -> 9;
            case 2 -> 10;
            default -> 120;
        };
    }
}
