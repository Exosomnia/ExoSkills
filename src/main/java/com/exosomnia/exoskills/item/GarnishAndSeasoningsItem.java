package com.exosomnia.exoskills.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GarnishAndSeasoningsItem extends Item {
    public GarnishAndSeasoningsItem() {
        super(new Properties().durability(8));
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack returnStack = itemStack.copy();
        returnStack.setDamageValue(itemStack.getDamageValue() + 1);
        return returnStack;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack itemStack) {
        return (itemStack.getDamageValue() + 1 < itemStack.getMaxDamage());
    }

    @Override
    public boolean isEnchantable(ItemStack itemStack) {
        return false;
    }
}
