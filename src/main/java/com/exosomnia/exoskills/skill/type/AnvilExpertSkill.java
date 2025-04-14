package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class AnvilExpertSkill extends BaseSkill {
    public AnvilExpertSkill(int id) {
        super(id);
    }

    public double breakReductionForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.75;
            case 1 -> 0.667;
            case 2 -> 0.50;
            default -> 0.0;
        };
    }

    public double costReductionAmountForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.15;
            case 1 -> 0.20;
            case 2 -> 0.25;
            default -> 1.0;
        };
    }

    public int costReductionMinForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            default -> 200;
        };
    }
}
