package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V", shift = At.Shift.BEFORE), cancellable = true)
    public void eatMixin(Item item, ItemStack itemStack, LivingEntity entity, CallbackInfo ci) {
        if (item.isEdible()) {
            FoodProperties foodproperties = itemStack.getFoodProperties(entity);
            int actualNutrition = foodproperties.getNutrition();
            float actualSaturation = foodproperties.getSaturationModifier();
            int masterwork = itemStack.getEnchantmentLevel(Registry.ENCHANTMENT_MASTERWORK.get());
            if (masterwork > 0) {
                actualNutrition = (int) Math.round(actualNutrition * 1.2);
                actualSaturation = (int) Math.round(actualSaturation * 1.2);
                int healthRestore = (int) Math.round((entity.getAttributeValue(Attributes.MAX_HEALTH) * 0.1));
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1 + (healthRestore * 12), 2));
            } else {
                int highQuality = itemStack.getEnchantmentLevel(Registry.ENCHANTMENT_HIGH_QUALITY.get());
                if (highQuality > 0) {
                    actualNutrition = (int) Math.round(actualNutrition * 1.2);
                    actualSaturation = (int) Math.round(actualSaturation * 1.1);
                }
            }
            ((FoodData)(Object)this).eat(actualNutrition, actualSaturation);
        }
        ci.cancel();
    }

//    @Inject(method = "<init>", at = @At("RETURN"))
//    private void logMethods(CallbackInfo ci) {
//        for (Method m : FoodData.class.getDeclaredMethods()) {
//            System.out.println("Method found: " + m.getName() + " " + m.toGenericString());
//        }
//    }
}
