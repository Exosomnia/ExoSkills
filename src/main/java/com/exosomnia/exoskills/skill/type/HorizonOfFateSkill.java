package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.loot.modifiers.SkillsLootModifier;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.base.BaseSkill;
import com.exosomnia.exoskills.skill.base.LootableSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class HorizonOfFateSkill extends BaseSkill implements LootableSkill {

    public HorizonOfFateSkill(int id) {
        super(id);
    }

    @Override
    public boolean validate(LootContext context) {
        Skills horizonOfFate = Skills.HORIZON_OF_FATE;
        ResourceLocation lootCause = ((ILootParamsMixin)((LootContextAccessor)context).getParams()).getCause();

        if (lootCause.equals(SkillsLootModifier.BLOCK_CAUSE)) {
            Entity lootEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (!(lootEntity instanceof ServerPlayer player)) return false;

            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            byte skillRank = playerSkillData.getSkillRank(horizonOfFate);

            if (playerSkillData.hasSkill(horizonOfFate) &&
                    player.getRandom().nextDouble() < blockChanceForRank(skillRank)) { return true; }
            return false;
        }
        else if (lootCause.equals(SkillsLootModifier.ENTITY_CAUSE)) {
            Entity lootEntity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
            if (!(lootEntity instanceof ServerPlayer player)) return false;

            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            byte skillRank = playerSkillData.getSkillRank(horizonOfFate);

            if (playerSkillData.hasSkill(horizonOfFate) &&
                    player.getRandom().nextDouble() < entityChanceForRank(skillRank)) { return true; }
            return false;
        }
        return false;
    }

    public double blockChanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.25;
            case 1 -> 0.33;
            default -> 1.0;
        };
    }

    public double entityChanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.02;
            case 1 -> 0.0333;
            default -> 1.0;
        };
    }
}
