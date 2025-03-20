package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class ExperiencedExplorerSkill extends BaseSkill {
    public ExperiencedExplorerSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.667;
            case 1 -> 0.75;
            default -> 1.0;
        };
    }

    public int xpForRank(byte rank) {
        return switch (rank) {
            case 0 -> 3;
            case 1 -> 4;
            default -> 20;
        };
    }
}
