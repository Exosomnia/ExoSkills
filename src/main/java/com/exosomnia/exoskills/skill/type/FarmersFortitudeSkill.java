package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class FarmersFortitudeSkill extends BaseSkill {
    public FarmersFortitudeSkill(int id) {
        super(id);
    }

    public double reductionAmount(byte rank) {
        return switch (rank) {
            case 0 -> 0.95;
            case 1 -> 0.925;
            default -> 0.0;
        };
    }
}
