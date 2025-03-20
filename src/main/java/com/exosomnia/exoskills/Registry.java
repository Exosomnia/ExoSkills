package com.exosomnia.exoskills;

import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exoskills.command.ExoSkillCommand;
import com.exosomnia.exoskills.effect.LootersLuckEffect;
import com.exosomnia.exoskills.effect.PatienceEffect;
import com.exosomnia.exoskills.enchantment.HighQualityEnchantment;
import com.exosomnia.exoskills.enchantment.MasterworkEnchantment;
import com.exosomnia.exoskills.item.*;
import com.exosomnia.exoskills.loot.conditions.SkillCondition;
import com.exosomnia.exoskills.loot.modifiers.SkillsLootModifier;
import com.exosomnia.exoskills.networking.PacketHandler;
import com.exosomnia.exoskills.recipe.SeasoningRecipe;
import com.exosomnia.exoskills.recipe.smithing.SmithingReinforceRecipe;
import com.exosomnia.exoskills.skill.type.OccultApothecarySkill;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.function.Function;

public class Registry {

    public static final TagKey<Block> ORE_BLOCK_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.bySeparator("forge:ores", ':'));
    public static final TagKey<Biome> CAVE_BIOME_TAG = TagKey.create(Registries.BIOME, ResourceLocation.bySeparator("forge:caves", ':'));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExoSkills.MODID);
    public static final RegistryObject<Item> ITEM_GARNISH_AND_SEASONINGS = ITEMS.register("garnish_and_seasonings", GarnishAndSeasoningsItem::new);
    public static final RegistryObject<Item> ITEM_ONYX = ITEMS.register("onyx", () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ITEM_HUSBANDRY_BOON_HEALTH = ITEMS.register("husbandry_boon_health", () -> new BoonItem(BoonItem.BoonAttributes.HEALTH));
    public static final RegistryObject<Item> ITEM_HUSBANDRY_BOON_ARMOR = ITEMS.register("husbandry_boon_armor", () -> new BoonItem(BoonItem.BoonAttributes.ARMOR));
    public static final RegistryObject<Item> ITEM_HUSBANDRY_BOON_ARMOR_TOUGHNESS = ITEMS.register("husbandry_boon_armor_toughness", () -> new BoonItem(BoonItem.BoonAttributes.ARMOR_TOUGHNESS));

    public static final RegistryObject<Item> ITEM_MINING_BOON_ATTACK = ITEMS.register("mining_boon_attack", () -> new BoonItem(BoonItem.BoonAttributes.ATTACK_DAMAGE));
    public static final RegistryObject<Item> ITEM_MINING_BOON_RANGED = ITEMS.register("mining_boon_ranged", () -> new BoonItem(BoonItem.BoonAttributes.RANGED_STRENGTH));
    public static final RegistryObject<Item> ITEM_MINING_BOON_MAGIC = ITEMS.register("mining_boon_magic", () -> new BoonItem(BoonItem.BoonAttributes.SPELL_DAMAGE));

    public static final RegistryObject<Item> ITEM_FISHING_BOON_SPEED = ITEMS.register("fishing_boon_speed", () -> new BoonItem(BoonItem.BoonAttributes.MOVEMENT_SPEED));
    public static final RegistryObject<Item> ITEM_FISHING_BOON_HEALING = ITEMS.register("fishing_boon_healing", () -> new BoonItem(BoonItem.BoonAttributes.HEALING_RECEIVED));
    public static final RegistryObject<Item> ITEM_FISHING_BOON_LUCK = ITEMS.register("fishing_boon_luck", () -> new BoonItem(BoonItem.BoonAttributes.LUCK));

    public static final RegistryObject<Item> ITEM_ARCANE_SINGULARITY = ITEMS.register("arcane_singularity", ArcaneSingularityItem::new);
    public static final RegistryObject<Item> ITEM_HORIZON_OF_FATE = ITEMS.register("horizon_of_fate", HorizonOfFateItem::new);

    public static final RegistryObject<Item> ITEM_LUCKY_ESSENCE = ITEMS.register("lucky_essence", LuckyEssenceItem::new);
    public static final RegistryObject<Item> ITEM_LUCKY_CLOVER = ITEMS.register("lucky_clover", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_LUCKY_GEM = ITEMS.register("lucky_gem", () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,
            ExoSkills.MODID);
    public static final RegistryObject<RecipeSerializer<SeasoningRecipe>> RECIPE_SEASONING = RECIPE_SERIALIZERS.register("seasoning_crafting",
            () -> new SimpleCraftingRecipeSerializer<>(SeasoningRecipe::new));
    public static final RegistryObject<RecipeSerializer<SmithingReinforceRecipe>> RECIPE_REINFORCE = RECIPE_SERIALIZERS.register("reinforce_smithing",
            SmithingReinforceRecipe.Serializer::new);

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ExoSkills.MODID);
    public static final RegistryObject<Enchantment> ENCHANTMENT_HIGH_QUALITY = ENCHANTMENTS.register("high_quality", HighQualityEnchantment::new);
    public static final RegistryObject<Enchantment> ENCHANTMENT_MASTERWORK = ENCHANTMENTS.register("masterwork", MasterworkEnchantment::new);

    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ExoSkills.MODID);
    public static final RegistryObject<LootItemConditionType> SKILL_CONDITION = LOOT_ITEM_CONDITIONS.register("skill_condition", () -> new LootItemConditionType(new SkillCondition.Serializer()));

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ExoSkills.MODID);
    public static final RegistryObject<Codec<SkillsLootModifier>> LOOT_MOD_SKILLS = GLOBAL_LOOT_MODS.register("skills_loot_mod", SkillsLootModifier.CODEC);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExoSkills.MODID);
    public static final RegistryObject<CreativeModeTab> TAB_SKILLS_EQUIPMENT = CREATIVE_TABS.register("skills_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("tab.exoskills.items"))
            .icon(() -> new ItemStack(ITEM_FISHING_BOON_SPEED.get()))
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> item : ITEMS.getEntries()) {
                    output.accept(new ItemStack(item.get()));
                }
            })
            .build());

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            ExoSkills.MODID);

    public static final RegistryObject<MobEffect> EFFECT_PATIENCE = MOB_EFFECTS.register("patience",
            PatienceEffect::new);
    public static final RegistryObject<MobEffect> EFFECT_LOOTERS_LUCK = MOB_EFFECTS.register("looters_luck",
            LootersLuckEffect::new );

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS,
            ExoSkills.MODID);

    public static final RegistryObject<Potion> POTION_ENHANCED_LUCK = POTIONS.register("enhanced_luck",
            () -> new Potion(new MobEffectInstance(MobEffects.LUCK, 6000, 0), new MobEffectInstance(EFFECT_LOOTERS_LUCK.get(), 6000, 0)));
    public static final RegistryObject<Potion> POTION_ENHANCED_LUCK_EXTENDED = POTIONS.register("enhanced_luck_extended",
            () -> new Potion(new MobEffectInstance(MobEffects.LUCK, 16000, 0), new MobEffectInstance(EFFECT_LOOTERS_LUCK.get(), 16000, 0)));

    //FALLBACK, IF INTEGRATION IS NOT LOADED, USE ATTACK SPEED INSTEAD
    public static Attribute ATTRIBUTE_MAGIC_DAMAGE = Attributes.ATTACK_SPEED;
    public static Attribute ATTRIBUTE_MANA_REGEN = Attributes.ATTACK_SPEED;
    public static Attribute ATTRIBUTE_STAMINA = Attributes.ATTACK_SPEED;

    public static Item[] husbandryBoons;
    public static Item[] miningBoons;
    public static Item[] fishingBoons;

    public static Item[] damagableItems;

    public static ImmutableMap<MobEffect, Function<MobEffectInstance, MobEffectInstance>> bonusEffectMappings;

    public static HashSet<Integrations> loadedIntegrations = new HashSet<>();

    public static void registerObjects(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        ITEMS.register(eventBus);
        LOOT_ITEM_CONDITIONS.register(eventBus);
        GLOBAL_LOOT_MODS.register(eventBus);
        CREATIVE_TABS.register(eventBus);
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);

        PacketHandler.register(); //Register our packets
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        ExoSkillCommand.register(event.getDispatcher());
    }

    public static void populateAfterRegistration() {
        husbandryBoons = new Item[]{Registry.ITEM_HUSBANDRY_BOON_HEALTH.get(), Registry.ITEM_HUSBANDRY_BOON_ARMOR.get(), Registry.ITEM_HUSBANDRY_BOON_ARMOR_TOUGHNESS.get()};
        miningBoons = new Item[]{Registry.ITEM_MINING_BOON_ATTACK.get(), Registry.ITEM_MINING_BOON_RANGED.get(), Registry.ITEM_MINING_BOON_MAGIC.get()};
        fishingBoons = new Item[]{Registry.ITEM_FISHING_BOON_HEALING.get(), Registry.ITEM_FISHING_BOON_SPEED.get(), Registry.ITEM_FISHING_BOON_LUCK.get()};

        ImmutableSet.Builder<Item> damagableItemsBuilder = new ImmutableSet.Builder<>();
        ForgeRegistries.ITEMS.getValues().forEach(item -> {
            if (item.canBeDepleted()) damagableItemsBuilder.add(item);
        });
        damagableItems = damagableItemsBuilder.build().toArray(new Item[0]);

        bonusEffectMappings = ImmutableMap.<MobEffect, Function<MobEffectInstance, MobEffectInstance>>builder()
                //Negative Effects
                .put(MobEffects.POISON, (poison) -> OccultApothecarySkill.createBonusEffect(poison, ExoArmory.REGISTRY.EFFECT_BLIGHTED.get()))
                .put(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), (blighted) -> OccultApothecarySkill.createBonusEffect(blighted, MobEffects.POISON))

                .put(MobEffects.MOVEMENT_SLOWDOWN, (slowness) -> OccultApothecarySkill.createBonusEffect(slowness, MobEffects.WEAKNESS))
                .put(MobEffects.WEAKNESS, (weakness) -> OccultApothecarySkill.createBonusEffect(weakness, MobEffects.MOVEMENT_SLOWDOWN))

                .put(MobEffects.HARM, (harm) -> OccultApothecarySkill.createBonusEffect(harm, ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 400 * (1 + harm.getAmplifier()), 0))
                .put(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), (vulnerable) -> OccultApothecarySkill.createBonusEffect(vulnerable, MobEffects.HARM, 1, 0))

                //Positive Effects
                .put(MobEffects.REGENERATION, (regen) -> OccultApothecarySkill.createBonusEffect(regen, MobEffects.ABSORPTION, 1200, 0))
                .put(MobEffects.ABSORPTION, (absorption) -> OccultApothecarySkill.createBonusEffect(absorption, MobEffects.REGENERATION))

                .put(MobEffects.DAMAGE_BOOST, (strength) -> OccultApothecarySkill.createBonusEffect(strength, MobEffects.FIRE_RESISTANCE))
                .put(MobEffects.FIRE_RESISTANCE, (fireResist) -> OccultApothecarySkill.createBonusEffect(fireResist, MobEffects.DAMAGE_BOOST))

                .put(MobEffects.HEAL, (heal) -> OccultApothecarySkill.createBonusEffect(heal, MobEffects.MOVEMENT_SPEED, 600 * (1 + heal.getAmplifier()), 0))
                .put(MobEffects.MOVEMENT_SPEED, (speed) -> OccultApothecarySkill.createBonusEffect(speed, MobEffects.HEAL, 1, 0))

                .put(ExoArmory.REGISTRY.EFFECT_EAGLE_EYE.get(), (eagleEye) -> OccultApothecarySkill.createBonusEffect(eagleEye, MobEffects.DAMAGE_RESISTANCE))
                .put(MobEffects.DAMAGE_RESISTANCE, (resistance) -> OccultApothecarySkill.createBonusEffect(resistance, ExoArmory.REGISTRY.EFFECT_EAGLE_EYE.get()))
                .build();
    }

    public static void handleIntegrations(Integrations integration) {
        LogUtils.getLogger().info("Attempting integration for: {}", integration.name());
        switch (integration) {
            case IRONS_SPELLBOOKS:
                ATTRIBUTE_MAGIC_DAMAGE = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.bySeparator("irons_spellbooks:spell_power", ':'));
                ATTRIBUTE_MANA_REGEN = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.bySeparator("irons_spellbooks:mana_regen", ':'));
                loadedIntegrations.add(Integrations.IRONS_SPELLBOOKS);
                break;
            case PUFFISH_ATTRIBUTES:
                ATTRIBUTE_STAMINA = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.bySeparator("puffish_attributes:stamina", ':'));
                loadedIntegrations.add(Integrations.PUFFISH_ATTRIBUTES);
                break;
            case LOOTR:
                loadedIntegrations.add(Integrations.LOOTR);
                break;
        }
    }

    public static boolean isLoaded(Integrations integration) {
        return loadedIntegrations.contains(integration);
    }

    public enum Integrations {
        IRONS_SPELLBOOKS,
        PUFFISH_ATTRIBUTES,
        LOOTR
    }
}
