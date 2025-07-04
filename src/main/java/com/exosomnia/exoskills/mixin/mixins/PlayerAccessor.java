package com.exosomnia.exoskills.mixin.mixins;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerAccessor {

    @Accessor("enchantmentSeed")
    void setEnchantmentSeed(int seed);
}
