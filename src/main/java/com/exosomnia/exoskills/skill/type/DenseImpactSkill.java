package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class DenseImpactSkill extends BaseSkill {
    public DenseImpactSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.50;
            case 1 -> 0.75;
            default -> 1.0;
        };
    }

    public double damageForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.25;
            case 1 -> 0.5;
            case 2 -> 0.75;
            default -> 2.0;
        };
    }

    public double radiusForRank(byte rank) {
        return switch (rank) {
            case 0 -> 2.5;
            case 1 -> 3.0;
            case 2 -> 3.5;
            default -> 6.0;
        };
    }
}
