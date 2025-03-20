package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.item.ArcaneSingularityItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

    @Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void releaseUsingSingularities(ItemStack bow, Level level, LivingEntity shooter, int time, CallbackInfo ci, Player player, boolean flag, ItemStack itemstack, int i, float f, boolean flag1, ArrowItem arrowitem, AbstractArrow abstractArrow, int j, int k){
        if (ArcaneSingularityItem.isSingularityActive(bow)) {
            if (ArcaneSingularityItem.hasSingularityEffect(bow, ArcaneSingularityItem.SingularityEffect.SOUL_BURN)) {
                abstractArrow.getPersistentData().putBoolean("SOUL_BURN", true);
            }
            else if (ArcaneSingularityItem.hasSingularityEffect(bow, ArcaneSingularityItem.SingularityEffect.CRIPPLING_STRIKE)) {
                abstractArrow.getPersistentData().putBoolean("CRIPPLING_STRIKE", true);
            }
        }
    }
}
