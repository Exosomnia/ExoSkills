package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class NaturesBlessingSkill extends BaseSkill {
    public NaturesBlessingSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.001;
            case 1 -> 0.0025;
            default -> 1.0;
        };
    }
}
