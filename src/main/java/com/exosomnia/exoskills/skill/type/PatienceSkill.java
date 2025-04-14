package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class PatienceSkill extends BaseSkill {
    public PatienceSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0, 1 -> 0.25;
            case 2 -> 0.333;
            default -> 1.0;
        };
    }

    public int maxDurationForRank(byte rank) {
        return switch (rank) {
            case 0 -> 9600;
            case 1 -> 12000;
            case 2 -> 14400;
            default -> Integer.MAX_VALUE;
        };
    }

    public int maxAmplifierForRank(byte rank) {
        return switch (rank) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 4;
            default -> Integer.MAX_VALUE;
        };
    }
}
