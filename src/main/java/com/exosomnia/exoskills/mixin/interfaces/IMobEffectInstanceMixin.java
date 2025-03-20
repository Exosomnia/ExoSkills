package com.exosomnia.exoskills.mixin.interfaces;

import net.minecraft.world.entity.Entity;

public interface IMobEffectInstanceMixin {

    void setSourceEntity(Entity sourceEntity);
    Entity getSourceEntity();

    void setForced(boolean forced);
    boolean isForced();

    void setApplicationSkipped(boolean skipped);
    boolean isApplicationSkipped();
}
