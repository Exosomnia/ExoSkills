package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class AquaticKnowledgeSkill extends BaseSkill {
    public AquaticKnowledgeSkill(int id) {
        super(id);
    }

    public double getAmountForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.5;
            case 1 -> 1.0;
            default -> 2.0;
        };
    }
}
