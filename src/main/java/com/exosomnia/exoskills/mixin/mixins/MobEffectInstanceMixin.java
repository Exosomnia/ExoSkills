package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.mixin.interfaces.IMobEffectInstanceMixin;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstanceMixin {

    Entity sourceEntity = null;
    boolean forced = false;
    boolean skipped = false;

    @Override
    public void setSourceEntity(Entity sourceEntity) { this.sourceEntity = sourceEntity; }

    @Override
    public Entity getSourceEntity() { return sourceEntity; }

    @Override
    public void setForced(boolean forced) { this.forced = forced; }

    @Override
    public boolean isForced() { return forced; }

    @Override
    public void setApplicationSkipped(boolean skipped) { this.skipped = skipped; }

    @Override
    public boolean isApplicationSkipped() { return skipped; }
}
