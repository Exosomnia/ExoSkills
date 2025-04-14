package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class ExpertRiderSkill extends BaseSkill {
    public ExpertRiderSkill(int id) {
        super(id);
    }

    public int modForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0;
            case 1 -> 1;
            default -> 2;
        };
    }
}
