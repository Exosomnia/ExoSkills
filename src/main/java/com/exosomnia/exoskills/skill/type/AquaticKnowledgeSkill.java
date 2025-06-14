package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class AquaticKnowledgeSkill extends BaseSkill {
    public AquaticKnowledgeSkill(int id) {
        super(id);
    }

    public double getAmountForRank(byte rank) {
        return switch (rank) {
            case 0 -> 1.0;
            case 1 -> 1.5;
            case 2 -> 2.0;
            default -> 2.0;
        };
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0, 1 -> 0.0;
            case 2 -> 0.25;
            default -> 1.0;
        };
    }
}
