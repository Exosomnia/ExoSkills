package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;

public class EnchantmentDivinationSkill extends BaseSkill {
    public EnchantmentDivinationSkill(int id) {
        super(id);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.333;
            case 1 -> 0.50;
            default -> 1.0;
        };
    }
}
