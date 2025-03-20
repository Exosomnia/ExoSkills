package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.base.BaseSkill;
import com.exosomnia.exoskills.skill.base.LootableSkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ArtifactAnglerSkill extends BaseSkill implements LootableSkill {

    public ArtifactAnglerSkill(int id) {
        super(id);
    }

    @Override
    public boolean validate(LootContext context) {
        if(!context.hasParam(LootContextParams.THIS_ENTITY)) return false;

        Entity lootEntity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(lootEntity instanceof ServerPlayer player)) return false;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        if (playerSkillData.hasSkill(Skills.ARTIFACT_ANGLER) &&
                player.getRandom().nextDouble() < chanceForRank(playerSkillData.getSkillRank(Skills.ARTIFACT_ANGLER))) { return true; }
        return false;
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.8;
            default -> 1.0;
        };
    }
}
