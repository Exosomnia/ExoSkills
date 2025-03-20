package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class AnvilExpertSkill extends BaseSkill {
    public AnvilExpertSkill(int id) {
        super(id);
    }

    public double breakReductionForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.75;
            case 1 -> 0.50;
            default -> 0.0;
        };
    }

    public double costReductionAmountForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.333;
            case 1 -> 0.5;
            default -> 1.0;
        };
    }

    public int costReductionMaxForRank(byte rank) {
        return switch (rank) {
            case 0 -> 5;
            case 1 -> 10;
            default -> 200;
        };
    }
}
