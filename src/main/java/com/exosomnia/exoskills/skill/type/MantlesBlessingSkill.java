package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class MantlesBlessingSkill extends BaseSkill {
    public MantlesBlessingSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.01;
            case 1 -> 0.02;
            default -> 1.0;
        };
    }
}
