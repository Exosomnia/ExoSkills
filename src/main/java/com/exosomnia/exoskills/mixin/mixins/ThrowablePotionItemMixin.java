package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.OccultApothecarySkill;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ThrowablePotionItem.class)
public abstract class ThrowablePotionItemMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void reuseChancePotion(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemStack) {
        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills occultApothecary = Skills.OCCULT_APOTHECARY;
        if (playerSkillData.hasSkill(occultApothecary) && level.random.nextDouble() < ((OccultApothecarySkill)occultApothecary.getSkill()).reuseChanceForRank(playerSkillData.getSkillRank(occultApothecary))) {
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()));
        }
    }
}
