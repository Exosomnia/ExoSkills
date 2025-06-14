package com.exosomnia.exoskills.skill.type;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.base.BaseSkill;
import com.exosomnia.exoskills.skill.base.LootableSkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootersLuckSkill extends BaseSkill implements LootableSkill {

    public LootersLuckSkill(int id) {
        super(id);
    }

    @Override
    public boolean validate(LootContext context) {
        ILootParamsMixin lootParams = ((ILootParamsMixin)((LootContextAccessor)context).getParams());
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof ServerPlayer player) || !lootParams.shouldLootModify()) { return false; }

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills lootersLuck = Skills.LOOTERS_LUCK;
        return playerSkillData.hasSkill(lootersLuck) && player.getRandom().nextDouble() < (chanceForRank(playerSkillData.getSkillRank(lootersLuck)) + player.getLuck() * 0.002);
    }

    public double chanceForRank(byte rank) {
        return switch (rank) {
            case 0 -> 0.03;
            case 1 -> 0.035;
            case 2 -> 0.04;
            default -> 1.0;
        };
    }
}
