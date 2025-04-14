package com.exosomnia.exoskills.loot.conditions;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class RankCondition implements LootItemCondition {

    private Skills skill;
    private int minRank;
    private LootContext.EntityTarget target;

    RankCondition(Skills skill, int minRank, LootContext.EntityTarget target) {
        this.skill = skill;
        this.minRank = minRank;
        this.target = target;
    }

    public LootItemConditionType getType() { return Registry.RANK_CONDITION.get(); }

    public Set<LootContextParam<?>> getReferencedContextParams() { return ImmutableSet.of(target.getParam()); }

    @Override
    public boolean test(LootContext lootContext) {
        if (!(lootContext.getParamOrNull(target.getParam()) instanceof ServerPlayer player)) return false;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        return playerSkillData.hasSkill(skill) && playerSkillData.getSkillRank(skill) >= minRank;
    }

    public static class Builder implements LootItemCondition.Builder {

        private Skills skill;
        private int minRank;
        private LootContext.EntityTarget target;

        public Builder setSkill(Skills skill) {
            this.skill = skill;
            return this;
        }

        public Builder setMinRank(int minRank) {
            this.minRank = minRank;
            return this;
        }

        public Builder setTarget(String target) {
            this.target = LootContext.EntityTarget.valueOf(target);
            return this;
        }

        public LootItemCondition build() {
            return new RankCondition(skill, minRank, target);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<RankCondition> {

        @Override
        public void serialize(JsonObject object, RankCondition condition, JsonSerializationContext context) {
            object.add("skill", context.serialize(condition.skill.name().toLowerCase()));
            object.add("rank", context.serialize(condition.minRank));
            object.add("entity", context.serialize(condition.target));
        }

        @Override
        public RankCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            Skills skill = Skills.valueOf(GsonHelper.getAsString(object, "skill").toUpperCase());
            int minRank = GsonHelper.getAsInt(object, "rank");
            LootContext.EntityTarget entityTarget = GsonHelper.getAsObject(object, "entity", context, LootContext.EntityTarget.class);
            return new RankCondition(skill, minRank, entityTarget);
        }
    }
}
