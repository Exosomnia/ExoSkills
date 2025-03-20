package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exolib.loot.LootTableInjectHandler;
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

public class GeomancersLuckSkill extends BaseSkill implements LootableSkill {

    public GeomancersLuckSkill(int id) {
        super(id);
    }

    @Override
    public boolean validate(LootContext context) {
        if(!context.hasParam(LootContextParams.THIS_ENTITY)) return false;

        Entity lootEntity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(lootEntity instanceof ServerPlayer player)) return false;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        byte skillRank = playerSkillData.getSkillRank(Skills.GEOMANCERS_LUCK);
        double luckMod = player.getAttributeValue(Attributes.LUCK) * luckModForRank(skillRank);
        if (playerSkillData.hasSkill(Skills.GEOMANCERS_LUCK) &&
                player.getRandom().nextDouble() < Math.min(0.333, chanceForRank(playerSkillData.getSkillRank(Skills.GEOMANCERS_LUCK)) + luckMod)) { return true; }
        return false;
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.1;
            case 1 -> 0.15;
            default -> 1.0;
        };
    }

    public double luckModForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.01;
            case 1 -> 0.02;
            default -> 0.04;
        };
    }
}
