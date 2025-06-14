package com.exosomnia.exoskills.effect;

import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exoskills.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PatienceEffect extends MobEffect {

    private final static String LUCK_UUID = "4fe40d77-fb06-4d5e-a855-03ab24ff8e1a";
    private final static String MANA_UUID = "73984488-958f-41af-a8aa-16350fb14de0";
    private final static String RANGED_UUID = "62849664-1870-41cd-91d4-612f06857fbc";
    private final static String CRITICAL_UUID = "609eb847-6118-4c0e-903d-3001bf70074c";

    public PatienceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x2C2491);

        this.addAttributeModifier(Attributes.LUCK,
                LUCK_UUID, 0.2, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(ExoArmory.REGISTRY.ATTRIBUTE_RANGED_STRENGTH.get(),
                RANGED_UUID, 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(ExoArmory.REGISTRY.ATTRIBUTE_PASSIVE_CRITICAL.get(),
                CRITICAL_UUID, 0.02, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static void handleIntegrations(MobEffect effect, Registry.Integrations integration) {
        if (integration.equals(Registry.Integrations.IRONS_SPELLBOOKS)) {
            effect.addAttributeModifier(Registry.ATTRIBUTE_MANA_REGEN,
                    MANA_UUID, 0.02, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
}
