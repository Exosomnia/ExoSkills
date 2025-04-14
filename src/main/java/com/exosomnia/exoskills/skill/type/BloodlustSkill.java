package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class BloodlustSkill extends BaseSkill {
    public BloodlustSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.04;
            case 1 -> 0.0425;
            case 2 -> 0.045;
            default -> 1.0;
        };
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
            case 0 -> 60;
            case 1 -> 70;
            case 2 -> 80;
            default -> 160;
        };
    }
}
