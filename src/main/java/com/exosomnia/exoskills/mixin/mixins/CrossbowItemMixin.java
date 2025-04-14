package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.QuickShotSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void releaseUsingCrossbow(Level level, LivingEntity entity, InteractionHand hand, ItemStack weapon, ItemStack projectileItem, float f1, boolean f2, float f3, float f4, float f5, CallbackInfo ci, boolean flag, Projectile projectile){
        if (projectile instanceof AbstractArrow arrow && entity instanceof ServerPlayer player && !entity.hasEffect(Registry.EFFECT_QUICK_SHOT.get())) {
            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            Skills quickShot = Skills.QUICK_SHOT;
            CompoundTag arrowTag = arrow.getPersistentData();
            if (playerSkillData.hasSkill(quickShot)) {
                arrowTag.putInt("QuickShot", ((QuickShotSkill)quickShot.getSkill()).shotsForRank(playerSkillData.getSkillRank(quickShot)));
            }
        }
    }
}
