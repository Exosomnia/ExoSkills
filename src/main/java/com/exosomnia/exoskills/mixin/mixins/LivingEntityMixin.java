package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.mixin.interfaces.IMobEffectInstanceMixin;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
    private void addSourceEntity(MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        IMobEffectInstanceMixin effectMixin = (IMobEffectInstanceMixin)effect;
        effectMixin.setSourceEntity(source);
    }
}
