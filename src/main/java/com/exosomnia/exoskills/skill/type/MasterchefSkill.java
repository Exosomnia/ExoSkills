package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.SkillManager;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.base.BaseSkill;
import com.exosomnia.exoskills.skill.base.LootableSkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class MasterchefSkill extends BaseSkill implements LootableSkill {

    public MasterchefSkill(int id) {
        super(id);
    }

    @Override
    public boolean validate(LootContext context) {
        if(!context.hasParam(LootContextParams.THIS_ENTITY)) return false;

        Entity lootEntity = context.getParam(LootContextParams.THIS_ENTITY);
        if (!(lootEntity instanceof ServerPlayer player)) return false;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        if (playerSkillData.hasSkill(Skills.MASTERCHEF) &&
                player.getRandom().nextDouble() < chanceForRank(playerSkillData.getSkillRank(Skills.MASTERCHEF))) { return true; }
        return false;
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.1;
            case 1 -> 0.2;
            default -> 1.0;
        };
    }
}
