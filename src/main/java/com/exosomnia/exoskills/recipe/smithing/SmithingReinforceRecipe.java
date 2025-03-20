package com.exosomnia.exoskills.recipe.smithing;

import com.exosomnia.exoskills.Registry;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class SmithingReinforceRecipe implements SmithingRecipe {

    private final ResourceLocation resourceLocation;
    private final Ingredient template;
    private final Ingredient addition;

    public SmithingReinforceRecipe(ResourceLocation resourceLocation, Ingredient template, Ingredient addition) {
        this.resourceLocation = resourceLocation;
        this.template = template;
        this.addition = addition;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.template.test(container.getItem(0)) && this.addition.test(container.getItem(2)) &&
                isBaseIngredient(container.getItem(1));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack itemStack = container.getItem(1).copy();
        itemStack.getOrCreateTag().putBoolean("Reinforcement", true);
        return itemStack;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        itemStack.getOrCreateTag().putBoolean("Reinforcement", true);
        return itemStack;
    }

    @Override
    public ResourceLocation getId() {
        return this.resourceLocation;
    }

    @Override
    public RecipeSerializer<?> getSerializer() { return Registry.RECIPE_REINFORCE.get(); }

    @Override
    public boolean isTemplateIngredient(ItemStack itemStack) {
        return this.template.test(itemStack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack itemStack) {
        return itemStack.isDamageableItem() && itemStack.hasTag() && !itemStack.getTag().getBoolean("Reinforcement");
    }

    @Override
    public boolean isAdditionIngredient(ItemStack itemStack) { return this.addition.test(itemStack); }

    public Ingredient getTemplate() { return template; }

    public Ingredient getAddition() { return addition; }

    public static class Serializer implements RecipeSerializer<SmithingReinforceRecipe> {
        public SmithingReinforceRecipe fromJson(ResourceLocation id, JsonObject object) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(object, "template"));
            Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getNonNull(object, "addition"));
            return new SmithingReinforceRecipe(id, ingredient, ingredient2);
        }

        public SmithingReinforceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient ingredient2 = Ingredient.fromNetwork(buffer);
            return new SmithingReinforceRecipe(id, ingredient, ingredient2);
        }

        public void toNetwork(FriendlyByteBuf buffer, SmithingReinforceRecipe recipe) {
            recipe.template.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
        }
    }
}
