package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class OreSenseSkill extends BaseSkill {
    public OreSenseSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.0333;
            case 1 -> 0.05;
            default -> 1.0;
        };
    }

    public int rangeForRank(byte rank) {
        return switch (rank) {
            case 0 -> 6;
            case 1 -> 7;
            default -> 16;
        };
    }

    public int durationForRank(byte rank) {
        return switch (rank) {
            case 0 -> 70;
            case 1 -> 80;
            default -> 600;
        };
    }
}
