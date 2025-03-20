package com.exosomnia.exoskills.loot.conditions;

import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.skill.SkillManager;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.base.LootableSkill;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class SkillCondition implements LootItemCondition {

    private Skills skill;
    private LootableSkill lootable;
    private int minRank;

    SkillCondition(Skills skill) {
        this(skill, -1);
    }

    SkillCondition(Skills skill, int minRank) {
        this.skill = skill;
        this.minRank = minRank;
        if (skill.getSkill() instanceof LootableSkill lootable) this.lootable = lootable;
        else { throw new IllegalArgumentException(String.format("%s is not a LootableSkill!", skill.name())); }
    }

    public LootItemConditionType getType() { return Registry.SKILL_CONDITION.get(); }

    public Set<LootContextParam<?>> getReferencedContextParams() { return ImmutableSet.of(LootContextParams.THIS_ENTITY); }

    @Override
    public boolean test(LootContext lootContext) {
        return minRank >= 0 ? lootable.validateWithRank(lootContext, minRank) : lootable.validate(lootContext);
    }

    public static class Builder implements LootItemCondition.Builder {

        private Skills skill;
        private int minRank = -1;

        public Builder setSkill(Skills skill) {
            this.skill = skill;
            return this;
        }

        public Builder setMinRank(int minRank) {
            this.minRank = minRank;
            return this;
        }

        public LootItemCondition build() {
            return minRank >= 0 ? new SkillCondition(skill, minRank) : new SkillCondition(skill);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SkillCondition> {

        @Override
        public void serialize(JsonObject object, SkillCondition condition, JsonSerializationContext context) {
            object.add("skill", context.serialize(condition.skill.name().toLowerCase()));
            object.add("rank", context.serialize(condition.minRank));
        }

        @Override
        public SkillCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            Skills skill = Skills.valueOf(GsonHelper.getAsString(object, "skill").toUpperCase());
            if (object.has("rank")) return new SkillCondition(skill, GsonHelper.getAsInt(object, "rank"));
            return new SkillCondition(skill);
        }
    }
}
