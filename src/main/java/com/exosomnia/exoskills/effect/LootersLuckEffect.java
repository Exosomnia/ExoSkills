package com.exosomnia.exoskills.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LootersLuckEffect extends MobEffect {

    private final static String LUCK_UUID = "054099ef-d347-47ac-ac73-501743c610be";

    public LootersLuckEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x85D966);

        this.addAttributeModifier(Attributes.LUCK,
                LUCK_UUID, 0.5, AttributeModifier.Operation.ADDITION);
    }
}
