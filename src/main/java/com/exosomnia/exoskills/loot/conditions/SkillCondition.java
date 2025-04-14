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

    SkillCondition(Skills skill) {
        this.skill = skill;
        if (skill.getSkill() instanceof LootableSkill lootable) this.lootable = lootable;
        else { throw new IllegalArgumentException(String.format("%s is not a LootableSkill!", skill.name())); }
    }

    public LootItemConditionType getType() { return Registry.SKILL_CONDITION.get(); }

    public Set<LootContextParam<?>> getReferencedContextParams() { return ImmutableSet.of(LootContextParams.THIS_ENTITY); }

    @Override
    public boolean test(LootContext lootContext) {
        return lootable.validate(lootContext);
    }

    public static class Builder implements LootItemCondition.Builder {

        private Skills skill;

        public Builder setSkill(Skills skill) {
            this.skill = skill;
            return this;
        }

        public LootItemCondition build() {
            return new SkillCondition(skill);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SkillCondition> {

        @Override
        public void serialize(JsonObject object, SkillCondition condition, JsonSerializationContext context) {
            object.add("skill", context.serialize(condition.skill.name().toLowerCase()));
        }

        @Override
        public SkillCondition deserialize(JsonObject object, JsonDeserializationContext context) {
            Skills skill = Skills.valueOf(GsonHelper.getAsString(object, "skill").toUpperCase());
            return new SkillCondition(skill);
        }
    }
}
