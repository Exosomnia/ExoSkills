package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class ArcaneOverloadSkill extends BaseSkill {
    public ArcaneOverloadSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.025;
            case 1 -> (1.0/30.0);
            default -> 1.0;
        };
    }
}
