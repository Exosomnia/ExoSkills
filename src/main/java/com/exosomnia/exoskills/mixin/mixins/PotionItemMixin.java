package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.mixin.interfaces.IMobEffectInstanceMixin;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.OccultApothecarySkill;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {

    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void reuseChancePotion(ItemStack itemStack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir, Player player) {
        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills occultApothecary = Skills.OCCULT_APOTHECARY;
        if (playerSkillData.hasSkill(occultApothecary) && level.random.nextDouble() < ((OccultApothecarySkill)occultApothecary.getSkill()).reuseChanceForRank(playerSkillData.getSkillRank(occultApothecary))) {
            cir.setReturnValue(itemStack);
            entity.gameEvent(GameEvent.DRINK);
        }
    }

    @Redirect(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean addPotionSource(LivingEntity entity, MobEffectInstance effect) {
        return entity.addEffect(effect, entity);
    }
}
