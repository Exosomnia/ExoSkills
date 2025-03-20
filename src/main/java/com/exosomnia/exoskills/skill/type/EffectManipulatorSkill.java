package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class EffectManipulatorSkill extends BaseSkill {
    public EffectManipulatorSkill(int id) {
        super(id);
    }

    public double reductionAmount(byte rank) {
        return switch (rank) {
            case 0 -> 0.9;
            case 1 -> 0.8;
            default -> 0.0;
        };
    }

    public double boostAmount(byte rank) {
        return switch (rank) {
            case 0 -> 1.25;
            case 1 -> 1.5;
            default -> 10.0;
        };
    }
}
