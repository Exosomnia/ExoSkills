package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class MinersCourageSkill extends BaseSkill {
    public MinersCourageSkill(int id) {
        super(id);
    }

    public double getBonusDamage(byte rank) {
        return switch (rank) {
            case 0 -> 0.05;
            case 1 -> 0.075;
            default -> 1.0;
        };
    }
}
