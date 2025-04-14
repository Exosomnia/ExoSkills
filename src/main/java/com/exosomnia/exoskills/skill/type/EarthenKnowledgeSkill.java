package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class EarthenKnowledgeSkill extends BaseSkill {
    public EarthenKnowledgeSkill(int id) {
        super(id);
    }

    public int maxForRank(byte rank) {
        return switch (rank) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 4;
            default -> 100;
        };
    }
}
