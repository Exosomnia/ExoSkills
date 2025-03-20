package com.exosomnia.exoskills.mixin.mixins;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

    //Only use in certain situations!!! Updating this value does not automatically send the data to the client and can cause desync!
    @Accessor("duration")
    void setDuration(int duration);
}
