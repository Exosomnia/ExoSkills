package com.exosomnia.exoskills.loot.modifiers;

import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.*;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SkillsLootModifier extends LootModifier {

    public static final Supplier<Codec<SkillsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec).apply(codec, SkillsLootModifier::new)));

    private static final ImmutableSet<ResourceLocation> BLACKLIST_TABLES = ImmutableSet.of(ResourceLocation.fromNamespaceAndPath("botania", "elementium_axe_beheading"));
    public static final ResourceLocation BLOCK_CAUSE = ResourceLocation.fromNamespaceAndPath("minecraft", "block");
    public static final ResourceLocation ENTITY_CAUSE = ResourceLocation.fromNamespaceAndPath("minecraft", "entity");
    public static final ResourceLocation FISHING_CAUSE = ResourceLocation.fromNamespaceAndPath("minecraft", "fishing");

    private static final TagKey<Block> CROPS_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("exoskills", "husbandry_blocks"));
    private static final TagKey<Item> SEEDS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "seeds"));

    public SkillsLootModifier(LootItemCondition[] conditions) {
        super(new LootItemCondition[0]);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (BLACKLIST_TABLES.contains(context.getQueriedLootTableId())) { return generatedLoot; }

        ILootParamsMixin lootParams = ((ILootParamsMixin)((LootContextAccessor)context).getParams());
        ResourceLocation lootCause = lootParams.getCause();
        if (!lootCause.getNamespace().equals("minecraft")) return generatedLoot;

        PlayerSkillData playerSkillData;
        RandomSource random;
        switch (lootCause.getPath()) {
            case "entity":
                Entity killingEntity = context.getParamOrNull(LootContextParams.DIRECT_KILLER_ENTITY);
                Entity killedEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
                if (!(killingEntity instanceof ServerPlayer player) || !(killedEntity instanceof LivingEntity defender) || !defender.getType().getCategory().equals(MobCategory.CREATURE)) { return generatedLoot; }

                random = player.getRandom();
                playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
                if (playerSkillData.hasSkill(Skills.GREEN_THUMB)) {
                    double chance = ((GreenThumbSkill) Skills.GREEN_THUMB.getSkill()).chanceForRank(playerSkillData.getSkillRank(Skills.GREEN_THUMB));
                    if (random.nextDouble() < chance) {
                        ObjectArrayList<ItemStack> copy = new ObjectArrayList<>(generatedLoot.size());
                        for (ItemStack loot : generatedLoot) {
                            copy.add(loot.copy());
                        }
                        generatedLoot.addAll(copy);
                    }
                }

                if (playerSkillData.hasSkill(Skills.NATURES_BLESSING)) {
                    double chance = ((NaturesBlessingSkill) Skills.NATURES_BLESSING.getSkill()).chanceForRank(playerSkillData.getSkillRank(Skills.NATURES_BLESSING));
                    if (random.nextDouble() < chance) {
                        generatedLoot.add(new ItemStack(Registry.husbandryBoons[random.nextInt(3)]));
                    }
                }
                break;
            case "block":
                Entity breakingEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
                BlockState brokenState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
                if (!(breakingEntity instanceof ServerPlayer player) || brokenState == null) { return generatedLoot; }

                if (brokenState.is(CROPS_TAG) && isGrown(brokenState)) {
                    //First check for earthen knowledge skill, then continue with loot checks
                    playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
                    Skills earthenKnowledge = Skills.EARTHEN_KNOWLEDGE;
                    if (playerSkillData.hasSkill(earthenKnowledge)) {
                        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
                        int max = ((EarthenKnowledgeSkill)earthenKnowledge.getSkill()).maxForRank(playerSkillData.getSkillRank(earthenKnowledge));
                        ExperienceOrb.award((ServerLevel) player.level(), origin == null ? player.position() : origin, player.getRandom().nextInt(max + 1));
                    }

                    random = player.getRandom();
                    if (playerSkillData.hasSkill(Skills.GREEN_THUMB)) {
                        double chance = ((GreenThumbSkill) Skills.GREEN_THUMB.getSkill()).chanceForRank(playerSkillData.getSkillRank(Skills.GREEN_THUMB));
                        if (random.nextDouble() < chance) {
                            ObjectArrayList<ItemStack> copy = new ObjectArrayList<>(generatedLoot.size());
                            for (ItemStack loot : generatedLoot) {
                                if (!loot.is(SEEDS_TAG)) copy.add(loot.copy());
                            }
                            generatedLoot.addAll(copy);
                        }
                    }

                    if (playerSkillData.hasSkill(Skills.NATURES_BLESSING)) {
                        double chance = ((NaturesBlessingSkill) Skills.NATURES_BLESSING.getSkill()).chanceForRank(playerSkillData.getSkillRank(Skills.NATURES_BLESSING));
                        if (random.nextDouble() < chance) {
                            generatedLoot.add(new ItemStack(Registry.husbandryBoons[random.nextInt(3)]));
                        }
                    }
                }
                else if (brokenState.is(Registry.ORE_BLOCK_TAG)) {
                    ItemStack toolItemStack = context.getParamOrNull(LootContextParams.TOOL);
                    if (toolItemStack != null && toolItemStack.getEnchantmentLevel(Enchantments.SILK_TOUCH) <= 0) {
                        playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
                        Skills mantlesBlessing = Skills.MANTLES_BLESSING;
                        if (playerSkillData.hasSkill(mantlesBlessing)) {
                            double chance = ((MantlesBlessingSkill)mantlesBlessing.getSkill()).chanceForRank(playerSkillData.getSkillRank(mantlesBlessing));
                            if (player.getRandom().nextDouble() < chance) {
                                generatedLoot.add(new ItemStack(Registry.miningBoons[player.getRandom().nextInt(3)]));
                            }
                        }
                    }
                }
                break;
            case "fishing":
                Entity fishingEntity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
                if (!(fishingEntity instanceof ServerPlayer player)) { return generatedLoot; }

                playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);

                Skills enchantedBobber = Skills.ENCHANTED_BOBBER;
                if (playerSkillData.hasSkill(enchantedBobber)) {
                    boolean hasEnchanted = false;
                    byte rank = playerSkillData.getSkillRank(enchantedBobber);
                    int maxHealth = ((EnchantedBobberSkill)enchantedBobber.getSkill()).getMaxHealth(rank);
                    int luckDuration = ((EnchantedBobberSkill)enchantedBobber.getSkill()).getLuckDuration(rank);
                    float currentHealth = player.getAbsorptionAmount();
                    if (currentHealth < maxHealth) {
                        for (ItemStack itemStack : generatedLoot) {
                            if (itemStack.isEnchanted() || itemStack.is(Items.ENCHANTED_BOOK)) {
                                hasEnchanted = true;
                                break;
                            }
                        }
                        if (hasEnchanted) {
                            player.setAbsorptionAmount(Math.min(currentHealth + 1.0F, maxHealth));
                            if (context.getLevel().random.nextBoolean()) {
                                MobEffectInstance currentLuck = player.getEffect(MobEffects.LUCK);
                                if (currentLuck != null) {
                                    player.addEffect(new MobEffectInstance(MobEffects.LUCK, currentLuck.getDuration() + luckDuration, currentLuck.getAmplifier(), true, false, true));
                                }
                                player.addEffect(new MobEffectInstance(MobEffects.LUCK, luckDuration, 0, true, false, true));
                            }
                        }
                    }
                }

                Skills oceansBlessing = Skills.OCEANS_BLESSING;
                if (playerSkillData.hasSkill(oceansBlessing)) {
                    double chance = ((OceansBlessingSkill)oceansBlessing.getSkill()).chanceForRank(playerSkillData.getSkillRank(oceansBlessing));
                    if (player.getRandom().nextDouble() < chance) {
                        generatedLoot.add(new ItemStack(Registry.fishingBoons[player.getRandom().nextInt(3)]));
                    }
                }
                break;
            case "chest":
                Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
                if (!(entity instanceof ServerPlayer player) || !lootParams.shouldLootModify()) { return generatedLoot; }

                playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);

                random = player.getRandom();
                Skills experiencedExplorer = Skills.EXPERIENCED_EXPLORER;
                if (playerSkillData.hasSkill(experiencedExplorer)) {
                    ExperiencedExplorerSkill skill = (ExperiencedExplorerSkill)experiencedExplorer.getSkill();
                    byte rank = playerSkillData.getSkillRank(experiencedExplorer);
                    double chance = skill.chanceForRank(rank);
                    if (random.nextDouble() < chance) {
                        int xp = skill.xpForRank(rank);
                        ExperienceOrb.award(player.serverLevel(), context.getParam(LootContextParams.ORIGIN), xp + random.nextInt(xp + 1));
                    }
                }
                break;
            default:
                break;
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    private boolean isGrown(BlockState state) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        }
        else if (state.is(Blocks.NETHER_WART)) {
            return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        }
        else if (state.is(Blocks.COCOA)) {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        }
        return false;
    }
}
