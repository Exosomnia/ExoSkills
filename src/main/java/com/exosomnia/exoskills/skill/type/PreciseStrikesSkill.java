package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class PreciseStrikesSkill extends BaseSkill {
    public PreciseStrikesSkill(int id) {
        super(id);
    }

    public int amountForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            default -> 7;
        };
    }

    public int durationForRank(byte rank) {
        return switch (rank) {
            case 0 -> 6;
            case 1 -> 7;
            case 2 -> 8;
            default -> 120;
        };
    }
}
