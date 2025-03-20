package com.exosomnia.exoskills.recipe;

import com.exosomnia.exoskills.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SeasoningRecipe extends CustomRecipe {

    public SeasoningRecipe(ResourceLocation resourceLocation, CraftingBookCategory craftingBookCategory) {
        super(resourceLocation, craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int seasonCount = 0;
        int foodCount = 0;
        for (ItemStack itemStack : container.getItems()) {
            if (itemStack.isEmpty()) {}
            else if (itemStack.isEdible() && !itemStack.isEnchanted()) foodCount++;
            else if (itemStack.is(Registry.ITEM_GARNISH_AND_SEASONINGS.get()) && (itemStack.getEnchantmentLevel(Registry.ENCHANTMENT_HIGH_QUALITY.get()) > 0 ||
                    itemStack.getEnchantmentLevel(Registry.ENCHANTMENT_MASTERWORK.get()) > 0)) seasonCount++;
        }
        return ((seasonCount == 1) && (foodCount == 1));
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack foodItem = ItemStack.EMPTY;
        ItemStack garnishItem = ItemStack.EMPTY;
        for (ItemStack itemStack : container.getItems()) {
            if (itemStack.isEmpty()) {}
            else if (itemStack.isEdible()) foodItem = itemStack;
            else if (itemStack.is(Registry.ITEM_GARNISH_AND_SEASONINGS.get())) garnishItem = itemStack;
        }
        foodItem = new ItemStack(foodItem.getItem());
        int masterwork = garnishItem.getEnchantmentLevel(Registry.ENCHANTMENT_MASTERWORK.get());
        if (masterwork > 0) {
            foodItem.enchant(Registry.ENCHANTMENT_MASTERWORK.get(), masterwork);
        }
        int highQuality = garnishItem.getEnchantmentLevel(Registry.ENCHANTMENT_HIGH_QUALITY.get());
        if (highQuality > 0) {
            foodItem.enchant(Registry.ENCHANTMENT_HIGH_QUALITY.get(), highQuality);
        }
        return foodItem;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.RECIPE_SEASONING.get();
    }
}
