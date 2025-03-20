package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class PickaxeMasterySkill extends BaseSkill {
    public PickaxeMasterySkill(int id) {
        super(id);
    }

    public double getBonusDamage(byte rank) {
        return switch (rank) {
            case 0 -> 0.25;
            case 1 -> 0.5;
            default -> 1.0;
        };
    }
}
