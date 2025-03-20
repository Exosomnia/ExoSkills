package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class NurturersTouchSkill extends BaseSkill {
    public NurturersTouchSkill(int id) {
        super(id);
    }

    public double getReductionAmount(byte rank) {
        return switch (rank) {
            case 0 -> 0.9;
            case 1 -> 0.8;
            default -> 0.0;
        };
    }
}
