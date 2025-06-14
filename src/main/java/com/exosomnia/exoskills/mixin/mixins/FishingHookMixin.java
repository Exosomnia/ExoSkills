package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.AquaticKnowledgeSkill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private boolean fishingXPSkill(Level level, Entity orb) {
        Player player = ((FishingHook)(Object)this).getPlayerOwner();

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills aquaticKnowledge = Skills.AQUATIC_KNOWLEDGE;
        double amount = playerSkillData.hasSkill(aquaticKnowledge) ? ((AquaticKnowledgeSkill)aquaticKnowledge.getSkill()).getAmountForRank(playerSkillData.getSkillRank(aquaticKnowledge)) : 0.0F;
        player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, (int)((1 + amount) * (player.getRandom().nextInt(6) + 1))));
        return true;
    }
}
