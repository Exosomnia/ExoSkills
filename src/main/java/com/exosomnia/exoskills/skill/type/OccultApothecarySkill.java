package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.skill.base.BaseSkill;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class OccultApothecarySkill extends BaseSkill {
    public OccultApothecarySkill(int id) {
        super(id);
    }

    public double effectChanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.333;
            case 1 -> 0.667;
            default -> 1.0;
        };
    }

    public double reuseChanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.1;
            case 1 -> 0.2;
            default -> 1.0;
        };
    }

    public static MobEffectInstance createBonusEffect(MobEffectInstance original, MobEffect bonusEffect) {
        return new MobEffectInstance(bonusEffect, original.getDuration(), original.getAmplifier(), original.isAmbient(), original.isVisible(), original.showIcon());
    }

    public static MobEffectInstance createBonusEffect(MobEffectInstance original, MobEffect bonusEffect, int duration, int amp) {
        return new MobEffectInstance(bonusEffect, duration, amp, original.isAmbient(), original.isVisible(), original.showIcon());
    }
}
