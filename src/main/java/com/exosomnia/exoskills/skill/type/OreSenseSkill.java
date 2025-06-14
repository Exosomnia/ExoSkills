package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class OreSenseSkill extends BaseSkill {
    public OreSenseSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.05;
            case 1 -> 0.075;
            default -> 1.0;
        };
    }

    public int rangeForRank(byte rank) {
        return switch (rank) {
            case 0 -> 8;
            case 1 -> 12;
            default -> 16;
        };
    }

    public int durationForRank(byte rank) {
        return switch (rank) {
            case 0 -> 80;
            case 1 -> 100;
            default -> 600;
        };
    }
}
