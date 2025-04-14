package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class DenseImpactSkill extends BaseSkill {
    public DenseImpactSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.333;
            case 1 -> 0.50;
            case 2 -> 0.667;
            default -> 1.0;
        };
    }

    public double damageForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.2;
            case 1 -> 0.25;
            case 2 -> 0.333;
            default -> 2.0;
        };
    }

    public double radiusForRank(byte rank) {
        return switch (rank) {
            case 0 -> 2.0;
            case 1 -> 2.25;
            case 2 -> 2.5;
            default -> 5.0;
        };
    }
}
