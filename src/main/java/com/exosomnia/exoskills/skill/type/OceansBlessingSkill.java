package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class OceansBlessingSkill extends BaseSkill {
    public OceansBlessingSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.005;
            case 1 -> 0.0125;
            default -> 1.0;
        };
    }
}
