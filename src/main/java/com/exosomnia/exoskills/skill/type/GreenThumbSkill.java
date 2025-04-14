package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class GreenThumbSkill extends BaseSkill {
    public GreenThumbSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.10;
            case 1 -> 0.25;
            default -> 1.0;
        };
    }
}
