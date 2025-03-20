package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class LifeMendingSkill extends BaseSkill {
    public LifeMendingSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.05;
            case 1 -> 0.0667;
            default -> 1.0;
        };
    }

    public double amountForRank(byte rank) {
        return switch (rank) {
            case 0 -> 1.0;
            case 1 -> 1.5;
            default -> 20.0;
        };
    }
}
