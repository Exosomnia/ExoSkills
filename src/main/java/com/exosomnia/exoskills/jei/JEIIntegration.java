package com.exosomnia.exoskills.jei;


import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.recipe.smithing.SmithingReinforceRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.IExtendableSmithingRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@JeiPlugin
public class JEIIntegration implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath(ExoSkills.MODID, "jei_plugin"); }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IExtendableSmithingRecipeCategory smithingCategory = registration.getSmithingCategory();
        smithingCategory.addExtension(SmithingReinforceRecipe.class, new SmithingReinforceCategoryExtension());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(Registry.ITEM_ONYX.get(), Component.translatable("item.exoskills.onyx.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_ARCANE_SINGULARITY.get(), Component.translatable("item.exoskills.arcane_singularity.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_HORIZON_OF_FATE.get(), Component.translatable("item.exoskills.horizon_of_fate.jei.info"));
        registration.addIngredientInfo(Registry.ITEM_LUCKY_ESSENCE.get(), Component.translatable("item.exoskills.lucky_essence.jei.info"));

        registration.addRecipes(RecipeTypes.BREWING, List.of(
                //Regular Luck
                new IJeiBrewingRecipe() {
                    @Override
                    public @Unmodifiable List<ItemStack> getPotionInputs() {
                        return List.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD));
                    }

                    @Override
                    public @Unmodifiable List<ItemStack> getIngredients() {
                        return List.of(new ItemStack(Registry.ITEM_LUCKY_ESSENCE.get()));
                    }

                    @Override
                    public ItemStack getPotionOutput() {
                        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LUCK);
                    }

                    @Override
                    public int getBrewingSteps() {
                        return 1;
                    }
                },
                //Enhanced Luck
                new IJeiBrewingRecipe() {
                    @Override
                    public @Unmodifiable List<ItemStack> getPotionInputs() {
                        return List.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LUCK));
                    }

                    @Override
                    public @Unmodifiable List<ItemStack> getIngredients() {
                        return List.of(new ItemStack(Registry.ITEM_LUCKY_CLOVER.get()));
                    }

                    @Override
                    public ItemStack getPotionOutput() {
                        return PotionUtils.setPotion(new ItemStack(Items.POTION), Registry.POTION_ENHANCED_LUCK.get());
                    }

                    @Override
                    public int getBrewingSteps() {
                        return 2;
                    }
                },
                //Enhanced Luck Extended
                new IJeiBrewingRecipe() {
                    @Override
                    public @Unmodifiable List<ItemStack> getPotionInputs() {
                        return List.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Registry.POTION_ENHANCED_LUCK.get()));
                    }

                    @Override
                    public @Unmodifiable List<ItemStack> getIngredients() {
                        return List.of(new ItemStack(Registry.ITEM_LUCKY_GEM.get()));
                    }

                    @Override
                    public ItemStack getPotionOutput() {
                        return PotionUtils.setPotion(new ItemStack(Items.POTION), Registry.POTION_ENHANCED_LUCK_EXTENDED.get());
                    }

                    @Override
                    public int getBrewingSteps() {
                        return 3;
                    }
                }
        ));
    }
}
