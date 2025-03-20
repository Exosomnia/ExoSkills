package com.exosomnia.exoskills.skill.base;

import net.minecraft.world.level.storage.loot.LootContext;

public interface LootableSkill {

    boolean validate(LootContext context);
    default boolean validateWithRank(LootContext context, int rank) {
        return validate(context);
    }
}
