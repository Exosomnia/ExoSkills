package com.exosomnia.exoskills.jei;

import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.recipe.smithing.SmithingReinforceRecipe;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.world.item.crafting.Ingredient;

public class SmithingReinforceCategoryExtension implements ISmithingCategoryExtension<SmithingReinforceRecipe> {

    @Override
    public <T extends IIngredientAcceptor<T>> void setTemplate(SmithingReinforceRecipe smithingReinforceRecipe, T ingredientAcceptor) {
        ingredientAcceptor.addIngredients(smithingReinforceRecipe.getTemplate());
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setBase(SmithingReinforceRecipe smithingReinforceRecipe, T ingredientAcceptor) {
        ingredientAcceptor.addIngredients(Ingredient.of(Registry.damagableItems));
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setAddition(SmithingReinforceRecipe smithingReinforceRecipe, T ingredientAcceptor) {
        ingredientAcceptor.addIngredients(smithingReinforceRecipe.getAddition());
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setOutput(SmithingReinforceRecipe smithingReinforceRecipe, T ingredientAcceptor) {
        ingredientAcceptor.addIngredients(Ingredient.of(Registry.damagableItems));
    }
}
