package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.base.BaseSkill;
import com.exosomnia.exoskills.skill.base.LootableSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class NomadicKnowledgeSkill extends BaseSkill implements LootableSkill {

    public NomadicKnowledgeSkill(int id) {
        super(id);
    }

    @Override
    public boolean validate(LootContext context) {
        ILootParamsMixin lootParams = ((ILootParamsMixin)((LootContextAccessor)context).getParams());
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof ServerPlayer player) || !lootParams.shouldLootModify()) { return false; }

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills nomadicKnowledge = Skills.NOMADIC_KNOWLEDGE;
        return playerSkillData.hasSkill(nomadicKnowledge) && player.getRandom().nextDouble() < chanceForRank(playerSkillData.getSkillRank(nomadicKnowledge));
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.01;
            case 1 -> 0.015;
            case 2 -> 0.02;
            default -> 1.0;
        };
    }
}
