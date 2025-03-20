package com.exosomnia.exoskills.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MasterworkEnchantment extends Enchantment {
    public MasterworkEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.VANISHABLE, EquipmentSlot.values());
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTradeable() {
        return false;
    }

    public boolean isDiscoverable() {
        return false;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    public boolean isAllowedOnBooks() {
        return false;
    }

    protected boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof HighQualityEnchantment) && super.checkCompatibility(enchantment);
    }
}
