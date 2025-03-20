package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.mixin.interfaces.IMobEffectInstanceMixin;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.OccultApothecarySkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin {

    @Inject(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V", shift = At.Shift.AFTER))
    private void instantEffectHealBonus(Entity directSource, Entity source, LivingEntity affected, int amp, double scale, CallbackInfo ci) {
        if (!(source instanceof ServerPlayer playerSource)) return;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(playerSource);
        Skills occultApothecary = Skills.OCCULT_APOTHECARY;
        if (playerSkillData.hasSkill(occultApothecary) && playerSource.getRandom().nextDouble() < ((OccultApothecarySkill)occultApothecary.getSkill()).reuseChanceForRank(playerSkillData.getSkillRank(occultApothecary))) {
            Function<MobEffectInstance, MobEffectInstance> bonusApplication = Registry.bonusEffectMappings.get(MobEffects.HEAL);
            if (bonusApplication != null) {
                MobEffectInstance bonusEffect = bonusApplication.apply(new MobEffectInstance(MobEffects.HEAL, 0, amp));
                IMobEffectInstanceMixin bonusEffectMixin = (IMobEffectInstanceMixin)bonusEffect;
                bonusEffectMixin.setSourceEntity(playerSource);
                bonusEffectMixin.setApplicationSkipped(true);
                affected.addEffect(bonusEffect);
            }
        }
    }

    @Inject(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void instantEffectHarmBonus(Entity directSource, Entity source, LivingEntity affected, int amp, double scale, CallbackInfo ci) {
        if (!(source instanceof ServerPlayer playerSource)) return;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(playerSource);
        Skills occultApothecary = Skills.OCCULT_APOTHECARY;
        if (playerSkillData.hasSkill(occultApothecary) && playerSource.getRandom().nextDouble() < ((OccultApothecarySkill)occultApothecary.getSkill()).reuseChanceForRank(playerSkillData.getSkillRank(occultApothecary))) {
            Function<MobEffectInstance, MobEffectInstance> bonusApplication = Registry.bonusEffectMappings.get(MobEffects.HARM);
            if (bonusApplication != null) {
                MobEffectInstance bonusEffect = bonusApplication.apply(new MobEffectInstance(MobEffects.HARM, 0, amp));
                IMobEffectInstanceMixin bonusEffectMixin = (IMobEffectInstanceMixin)bonusEffect;
                bonusEffectMixin.setSourceEntity(playerSource);
                bonusEffectMixin.setApplicationSkipped(true);
                affected.addEffect(bonusEffect);
            }
        }
    }
}
