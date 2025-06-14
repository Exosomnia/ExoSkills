package com.exosomnia.exoskills.skill;

import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exoarmory.utils.TargetUtils;
import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.exosomnia.exolib.particles.shapes.ParticleShapeOptions;
import com.exosomnia.exolib.particles.shapes.ParticleShapeRing;
import com.exosomnia.exolib.utils.ExperienceUtils;
import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.action.UpdateAgeAction;
import com.exosomnia.exoskills.item.ArcaneSingularityItem;
import com.exosomnia.exoskills.mixin.interfaces.IMobEffectInstanceMixin;
import com.exosomnia.exoskills.mixin.mixins.MobEffectInstanceAccessor;
import com.exosomnia.exoskills.mixin.mixins.PlayerAccessor;
import com.exosomnia.exoskills.networking.PacketHandler;
import com.exosomnia.exoskills.networking.packets.OreSensePacket;
import com.exosomnia.exoskills.skill.type.*;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.ASMEventHandler;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ExoSkills.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkillEventsHandler {

    static final TagKey<DamageType> IS_EXPLOSION_TYPE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("minecraft", "is_explosion"));
    static final UUID EXPERT_RIDER_UUID = UUID.fromString("7644e2c5-3b33-4035-8764-d6f7e03e8dba");
    static final AttributeModifier[] EXPERT_RIDER_MODS = new AttributeModifier[]{
            new AttributeModifier(EXPERT_RIDER_UUID, "expert_rider", 0.15, AttributeModifier.Operation.MULTIPLY_BASE),
            new AttributeModifier(EXPERT_RIDER_UUID, "expert_rider", 0.25, AttributeModifier.Operation.MULTIPLY_BASE),
            new AttributeModifier(EXPERT_RIDER_UUID, "expert_rider", 2.0, AttributeModifier.Operation.MULTIPLY_BASE)
    };

    @SubscribeEvent
    public static void playerProjectileImpactEvent(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        ProjectileImpactEvent.ImpactResult impactResult = event.getImpactResult();
        if (!level.isClientSide && (impactResult.equals(ProjectileImpactEvent.ImpactResult.DEFAULT) || impactResult.equals(ProjectileImpactEvent.ImpactResult.STOP_AT_CURRENT))
                && event.getRayTraceResult() instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof LivingEntity defender) {
                if (defender instanceof ServerPlayer player) {
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (!slot.isArmor()) continue;
                        ItemStack armor = player.getItemBySlot(slot);
                        if (!armor.isEmpty() && ArcaneSingularityItem.hasSingularityEffect(armor, ArcaneSingularityItem.SingularityEffect.SPECTRAL_FORM) &&
                                ArcaneSingularityItem.isSingularityActive(armor)) {
                            if (level.random.nextDouble() < 0.1) {
                                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                                break;
                            }
                        }
                    }
                }
                Projectile projectile = event.getProjectile();
                CompoundTag projectileData = projectile.getPersistentData();
                if (projectileData.getBoolean("SOUL_BURN")) {
                    defender.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_FIRE_VULNERABILITY.get(), 120, 1));
                }
                if (projectileData.getBoolean("CRIPPLING_STRIKE")) {
                    defender.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                }
                if (projectileData.contains("QuickShot")) {
                    int shots = projectileData.getInt("QuickShot");
                    Entity owner = projectile.getOwner();
                    if (!(owner instanceof LivingEntity livingOwner) || livingOwner.hasEffect(Registry.EFFECT_QUICK_SHOT.get())) return;
                    livingOwner.addEffect(new MobEffectInstance(Registry.EFFECT_QUICK_SHOT.get(), 200, shots, true, false, true));
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerHurtEvent(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            //Check for armor singularities
            float singularityReduction = 1.0F;
            RandomSource random = player.getRandom();
            boolean unbreakable = false, thermal = false;
            EquipmentSlot unbreakableSlot = null;
            Consumer<EquipmentSlot> unbreakableAction = (armorSlot) -> player.getItemBySlot(armorSlot).hurtAndBreak(Math.round(event.getAmount() * 0.25F), player, (consumer) -> consumer.broadcastBreakEvent(armorSlot));
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor()) continue;
                ItemStack armor = player.getItemBySlot(slot);
                if (!armor.isEmpty() && ArcaneSingularityItem.isSingularityActive(armor)) {
                    Enchantment enchantment = ArcaneSingularityItem.getSingularityEnchantment(armor);
                    switch (ArcaneSingularityItem.getSingularityEffect(enchantment)) {
                        case UNBREAKABLE_BOND:
                            if (!unbreakable && random.nextDouble() < 0.075) {
                                singularityReduction -= 0.25F;
                                unbreakable = true;
                                unbreakableSlot = slot;
                            }
                            break;
                        case SPECTRAL_FORM:
                        case SHOCK_ABSORPTION:
                            singularityReduction -= 0.08F;
                            break;
                        case THERMAL_CONDUCTIVITY:
                            singularityReduction -= 0.08F;
                            if (!thermal && random.nextDouble() < 0.2) {
                                thermal = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            if (unbreakable) {
                unbreakableAction.accept(unbreakableSlot);
                player.playNotifySound(SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.0F, 1.5F);
            }
            if (thermal && event.getSource().getEntity() != null) { ExoArmory.REGISTRY.ABILITY_SOLAR_FLARE.createSolarFlare(player.position(), player.serverLevel(), 2, player, null); }
            event.setAmount(event.getAmount() * singularityReduction);

            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);

            Skills farmersFortitude = Skills.FARMERS_FORTITUDE;
            if (playerSkillData.hasSkill(farmersFortitude) && player.getFoodData().getFoodLevel() < 20) {
                float fortitudeAmount = (float) ((FarmersFortitudeSkill) farmersFortitude.getSkill()).reductionAmount(playerSkillData.getSkillRank(farmersFortitude));
                event.setAmount(event.getAmount() * fortitudeAmount);
            }

            Skills explosivesExpert = Skills.EXPLOSIVES_EXPERT;
            if (playerSkillData.hasSkill(explosivesExpert) && event.getSource().is(IS_EXPLOSION_TYPE)) {
                float explosiveReductionAmount = (float) ((ExplosivesExpertSkill) explosivesExpert.getSkill()).reductionAmount(playerSkillData.getSkillRank(explosivesExpert));
                event.setAmount(event.getAmount() * explosiveReductionAmount);
            }
        }
        else if (event.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            Level level = player.level();
            ItemStack handItemStack = player.getMainHandItem();

            Skills minersCourage = Skills.MINERS_COURAGE;
            if (playerSkillData.hasSkill(minersCourage) && level.getBiome(player.blockPosition()).is(Registry.CAVE_BIOME_TAG)) {
                float courageAmount = (float) ((MinersCourageSkill) minersCourage.getSkill()).getBonusDamage(playerSkillData.getSkillRank(minersCourage));
                event.setAmount(event.getAmount() * (1.0F + courageAmount));
            }

            Skills pickaxeMastery = Skills.PICKAXE_MASTERY;
            if (playerSkillData.hasSkill(pickaxeMastery) && handItemStack.getItem() instanceof PickaxeItem) {
                float bonusDamage = (float)((PickaxeMasterySkill) pickaxeMastery.getSkill()).getBonusDamage(playerSkillData.getSkillRank(pickaxeMastery));
                event.setAmount(event.getAmount() * (1.0F + bonusDamage));
            }

            if (ArcaneSingularityItem.hasSingularityEffect(handItemStack, ArcaneSingularityItem.SingularityEffect.FLAWLESS_BLADE)) {
                if (ArcaneSingularityItem.isSingularityActive(handItemStack)) {
                    event.setAmount(event.getAmount() + 0.5F);
                }
            }
            else if (ArcaneSingularityItem.hasSingularityEffect(handItemStack, ArcaneSingularityItem.SingularityEffect.SOUL_BURN)) {
                if (ArcaneSingularityItem.isSingularityActive(handItemStack)) {
                    event.getEntity().addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_FIRE_VULNERABILITY.get(), 120, 0));
                }
            }
            else if (ArcaneSingularityItem.hasSingularityEffect(handItemStack, ArcaneSingularityItem.SingularityEffect.CRIPPLING_STRIKE)) {
                if (ArcaneSingularityItem.isSingularityActive(handItemStack)) {
                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                }
            }
        }

        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && arrow.getPersistentData().contains("ImpactData") && !arrow.level().isClientSide) {
            LivingEntity defender = event.getEntity();
            ServerLevel level = (ServerLevel)defender.level();
            Tag impactData = arrow.getPersistentData().get("ImpactData");
            if (!(impactData instanceof CompoundTag impactDataCompound) || impactDataCompound.getBoolean("Activated")) return;

            impactDataCompound.putBoolean("Activated", true);
            double damage = impactDataCompound.getDouble("Damage");
            double radius = impactDataCompound.getDouble("Radius");
            if (damage <= 0 || radius <= 0) return;

            ParticleShapePacket packet = new ParticleShapePacket(new ParticleShapeRing(ParticleTypes.CRIT, defender.position().add(0, 0.25, 0), new ParticleShapeOptions.Ring((float)radius, 64)));
            for (ServerPlayer player : level.players()) {
                com.exosomnia.exolib.networking.PacketHandler.sendToPlayer(packet, player);
            }

            level.playSound(null, defender.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 0.25F, 0.75F);

            Entity arrowOwner = arrow.getOwner();
            for (LivingEntity nearbyEntity : level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, defender, defender.getBoundingBox().inflate(radius, radius, radius))) {
                if (TargetUtils.validTarget((arrowOwner instanceof LivingEntity) ? (LivingEntity)arrowOwner : null, nearbyEntity) && nearbyEntity.distanceTo(defender) <= radius) {
                    DamageSource damageSource = defender.level().damageSources().arrow(arrow, arrowOwner);
                    nearbyEntity.hurt(damageSource, (float)(event.getAmount() * damage));
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerDamageEvent(LivingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getSource().is(IS_EXPLOSION_TYPE)) {
            boolean shock = false;
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor()) continue;
                ItemStack armor = player.getItemBySlot(slot);
                if (!armor.isEmpty() && ArcaneSingularityItem.isSingularityActive(armor) &&
                        ArcaneSingularityItem.hasSingularityEffect(armor, ArcaneSingularityItem.SingularityEffect.SHOCK_ABSORPTION)) {
                    shock = true;
                    break;
                }
            }
            if (shock) {
                player.setAbsorptionAmount(Math.min(8, player.getAbsorptionAmount() + (event.getAmount() * .20F)));
            }
        }
    }

    @SubscribeEvent
    public static void playerKillEvent(LivingDeathEvent event) {
        LivingEntity defender = event.getEntity();
        DamageSource source = event.getSource();
        Level level = defender.level();
        if (level.isClientSide) return;
        if (!(source.getEntity() instanceof ServerPlayer player) || !source.is(DamageTypes.PLAYER_ATTACK)) return;

        if (defender.getType().getCategory().equals(MobCategory.MONSTER) ||
                (defender instanceof NeutralMob neutralMob && neutralMob.isAngryAt(player))) {
            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            Skills bloodlust = Skills.BLOODLUST;
            if (!playerSkillData.hasSkill(bloodlust)) return;

            byte rank = playerSkillData.getSkillRank(bloodlust);
            BloodlustSkill skill = (BloodlustSkill) bloodlust.getSkill();
            if (level.random.nextDouble() < skill.chanceForRank(rank)) {
                int amplifier = skill.amountForRank(rank);
                int duration = skill.durationForRank(rank);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, amplifier));
                level.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 0.33F, 1.75F);
            }
        }
    }

    @SubscribeEvent
    public static void playerTrampleEvent(BlockEvent.FarmlandTrampleEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        event.setCanceled(ExoSkills.SKILL_MANAGER.getSkillData(player).hasSkill(Skills.CAREFUL_FOOTING));
    }

    @SubscribeEvent
    public static void playerBreedEvent(BabyEntitySpawnEvent event) {
        if (!(event.getCausedByPlayer() instanceof ServerPlayer player)) { return; }

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills nurturersTouch = Skills.NURTURERS_TOUCH;
        if (!playerSkillData.hasSkill(nurturersTouch)) { return; }

        double reduction = ((NurturersTouchSkill)nurturersTouch.getSkill()).getReductionAmount(playerSkillData.getSkillRank(nurturersTouch));

        AgeableMob child = event.getChild();
        Mob parentA = event.getParentA();
        Mob parentB = event.getParentB();

        if (playerSkillData.hasSkill(Skills.NATURES_BLESSING)) {
            Level level = child.level();
            double chance = ((NaturesBlessingSkill) Skills.NATURES_BLESSING.getSkill()).chanceForRank(playerSkillData.getSkillRank(Skills.NATURES_BLESSING));
            if (level.random.nextDouble() < chance) {
                ItemEntity boonDrop = new ItemEntity(EntityType.ITEM, level);
                boonDrop.setItem(new ItemStack(Registry.husbandryBoons[level.random.nextInt(3)]));
                boonDrop.setPos(parentA.getEyePosition());
                level.addFreshEntity(boonDrop);
            }
        }

        ExoLib.SERVER_SCHEDULE_MANAGER.scheduleAction(new UpdateAgeAction(child,
                (parentA instanceof AgeableMob parentAAge) ? parentAAge : null,
                (parentB instanceof AgeableMob parentBAge) ? parentBAge : null, reduction), 1);
    }

    @SubscribeEvent
    public static void playerTameEvent(AnimalTameEvent event) {
        Player player = event.getTamer();
        Level level = player.level();
        if (level.isClientSide) { return; }

        Animal tamed = event.getAnimal();
        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills earthenKnowledge = Skills.EARTHEN_KNOWLEDGE;
        if (playerSkillData.hasSkill(earthenKnowledge)) {
            ExperienceOrb.award((ServerLevel) level, tamed.position(), level.getRandom().nextInt(7) + 1);
        }

        if (playerSkillData.hasSkill(Skills.NATURES_BLESSING)) {
            double chance = ((NaturesBlessingSkill) Skills.NATURES_BLESSING.getSkill()).chanceForRank(playerSkillData.getSkillRank(Skills.NATURES_BLESSING));
            if (level.random.nextDouble() < chance) {
                ItemEntity boonDrop = new ItemEntity(EntityType.ITEM, level);
                boonDrop.setItem(new ItemStack(Registry.husbandryBoons[level.random.nextInt(3)]));
                boonDrop.setPos(tamed.getEyePosition());
                level.addFreshEntity(boonDrop);
            }
        }
    }

    @SubscribeEvent
    public static void playerBreakEvent(BlockEvent.BreakEvent event) {
        BlockState blockState = event.getState();
        if (!(event.getPlayer() instanceof ServerPlayer player) || !blockState.is(Registry.ORE_BLOCK_TAG) ||
                !player.hasCorrectToolForDrops(blockState)) { return; }

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills oreSense = Skills.ORE_SENSE;
        if (playerSkillData.hasSkill(oreSense)) {
            byte rank = playerSkillData.getSkillRank(Skills.ORE_SENSE);
            double chance = ((OreSenseSkill)Skills.ORE_SENSE.getSkill()).chanceForRank(rank);
            if (player.getRandom().nextDouble() < chance) {
                player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 0.75F);
                PacketHandler.sendToPlayer(new OreSensePacket(blockState.getBlock(), rank), player);
            }
        }
    }

    @SubscribeEvent
    public static void playerFishItem(ItemFishedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) { return; }

        RandomSource random = player.getRandom();
        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills patience = Skills.PATIENCE;
        Skills aquaticKnowledge = Skills.AQUATIC_KNOWLEDGE;
        if (playerSkillData.hasSkill(patience)) {
            byte rank = playerSkillData.getSkillRank(patience);
            double chance = ((PatienceSkill)patience.getSkill()).chanceForRank(rank);
            if (random.nextDouble() < chance) {
                player.playNotifySound(SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS, 1.0F, 1.75F);
                int maxDuration = ((PatienceSkill)patience.getSkill()).maxDurationForRank(rank);
                int maxAmplifier = ((PatienceSkill)patience.getSkill()).maxAmplifierForRank(rank);
                MobEffectInstance currentPatience = player.getEffect(Registry.EFFECT_PATIENCE.get());
                if (currentPatience != null) {
                    player.addEffect(new MobEffectInstance(Registry.EFFECT_PATIENCE.get(), Math.min(maxDuration, currentPatience.getDuration() + 1800), Math.min(maxAmplifier, currentPatience.getAmplifier() + 1), true, false, true));
                }
                player.addEffect(new MobEffectInstance(Registry.EFFECT_PATIENCE.get(), 1800, 0, true, false, true));
            }
        }

        if (playerSkillData.hasSkill(aquaticKnowledge)) {
            byte rank = playerSkillData.getSkillRank(aquaticKnowledge);
            double chance = ((AquaticKnowledgeSkill)patience.getSkill()).chanceForRank(rank);
            if (random.nextDouble() < chance) {
                event.getDrops().add(new ItemStack(Items.EXPERIENCE_BOTTLE));
            }
        }
    }

    @SubscribeEvent
    public static void anvilEvent(final AnvilRepairEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills anvilExpert = Skills.ANVIL_EXPERT;
        double reduction = playerSkillData.hasSkill(anvilExpert) ? ((AnvilExpertSkill)anvilExpert.getSkill()).breakReductionForRank(playerSkillData.getSkillRank(anvilExpert)) : 1.0;
        event.setBreakChance((float)(0.18 * reduction));
    }

    @SubscribeEvent
    public static void playerRerollXPSeed(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        Level level = player.level();
        BlockPos pos = event.getHitVec().getBlockPos();
        if (level.isClientSide || !itemStack.is(Items.AMETHYST_SHARD) || !level.getBlockState(pos).is(Blocks.ENCHANTING_TABLE)) { return; }

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills enchantmentDivination = Skills.ENCHANTMENT_DIVINATION;
        if (playerSkillData.hasSkill(enchantmentDivination)) {
            double chance = ((EnchantmentDivinationSkill)enchantmentDivination.getSkill()).chanceForRank(playerSkillData.getSkillRank(enchantmentDivination));

            event.setCanceled(true);
            ServerPlayer serverPlayer = (ServerPlayer) player;
            PlayerAccessor playerAccessor = (PlayerAccessor) player;
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 particlePos = pos.getCenter().add(0, 0.75, 0);
            if (level.random.nextDouble() < chance) {
                serverPlayer.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 0.5F);
                serverLevel.sendParticles(serverPlayer, ParticleTypes.WITCH, false, particlePos.x, particlePos.y, particlePos.z, 15, 0, 0, 0, 2.0);
                playerAccessor.setEnchantmentSeed(level.random.nextInt());
            } else {
                serverPlayer.playNotifySound(SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
                serverLevel.sendParticles(serverPlayer, ParticleTypes.SMOKE, false, particlePos.x, particlePos.y, particlePos.z, 15, 0, 0, 0, 0.025);
            }
            itemStack.shrink(1);
        }
    }

    /*
        Singularity application in anvils
    */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void anvilUpdateSingularityEvent(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        Item rightItem = right.getItem();

        if (rightItem instanceof ArcaneSingularityItem && ArcaneSingularityItem.hasSingularityData(right) && !ArcaneSingularityItem.hasSingularityData(left)) {
            Enchantment requiredEnchantment = ArcaneSingularityItem.getSingularityEnchantment(right);
            if (requiredEnchantment != null && left.getEnchantmentLevel(requiredEnchantment) >= requiredEnchantment.getMaxLevel()) {
                event.setCost(50);
                ItemStack result = left.copy();
                CompoundTag newSingularity = ArcaneSingularityItem.getSingularityTag(right);
                newSingularity.putBoolean("Active", true);
                result.getOrCreateTag().put("SingularityData", newSingularity);
                event.setOutput(result);
            }
            else { event.setOutput(ItemStack.EMPTY); }
        }
    }

    @SubscribeEvent
    public static void mobEffectAdded(MobEffectEvent.Applicable event) {
        IMobEffectInstanceMixin effect = (IMobEffectInstanceMixin)event.getEffectInstance();
        //If this effect is forced, bypass any restrictions
        if (effect.isForced()) {
            event.setResult(Event.Result.ALLOW);
            return;
        }

        LivingEntity effectEntity = event.getEntity();
        boolean isTargetPlayerOrTamed = ((effectEntity instanceof ServerPlayer) || ((effectEntity instanceof TamableAnimal tamed) && tamed.isTame()));

        //Check if we are applying a harmful effect to a player from another, and if so, cancel if they have the required skill.
        Entity sourceEntity = effect.getSourceEntity();
        if (event.getEffectInstance().getEffect().getCategory().equals(MobEffectCategory.HARMFUL) && isTargetPlayerOrTamed) {
            ServerPlayer sourcePlayer;
            //If the causing entity is from an AreaEffectCloud, use its owner as the source rather than itself
            if (sourceEntity instanceof AreaEffectCloud areaEffectCloud && areaEffectCloud.getOwner() instanceof ServerPlayer cloudPlayer) {
                sourcePlayer = cloudPlayer;
            }
            //Othewise, just check if the source is a player directly
            else if (sourceEntity instanceof ServerPlayer directPlayer) {
                sourcePlayer = directPlayer;
            }
            //And if neither, no further logic needed
            else { return; }
            PlayerSkillData sourceSkillData = ExoSkills.SKILL_MANAGER.getSkillData(sourcePlayer);
            Skills occultApothecary = Skills.OCCULT_APOTHECARY;
            if (sourceSkillData.hasSkill(occultApothecary)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void mobEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance effect = event.getEffectInstance();
        IMobEffectInstanceMixin mobEffectInstanceMixin = (IMobEffectInstanceMixin)effect;
        Entity sourceEntity = mobEffectInstanceMixin.getSourceEntity();
        boolean isPlayerSource = (sourceEntity instanceof ServerPlayer);

        LivingEntity effectEntity = event.getEntity();
        boolean isPlayerApplication = (effectEntity instanceof ServerPlayer);
        if (!isPlayerSource && !isPlayerApplication) return;

        //Check if a player is applying the effect, if so, look for bonus effects if they have the skill
        if (isPlayerSource && !mobEffectInstanceMixin.isApplicationSkipped()) {
            ServerPlayer sourcePlayer = (ServerPlayer)sourceEntity;
            PlayerSkillData sourceSkillData = ExoSkills.SKILL_MANAGER.getSkillData(sourcePlayer);
            Skills occultApothecary = Skills.OCCULT_APOTHECARY;
            if (sourceSkillData != null && sourceSkillData.hasSkill(occultApothecary) && sourcePlayer.getRandom().nextDouble() < ((OccultApothecarySkill)occultApothecary.getSkill()).effectChanceForRank(sourceSkillData.getSkillRank(occultApothecary))) {
                Function<MobEffectInstance, MobEffectInstance> bonusApplication = Registry.bonusEffectMappings.get(effect.getEffect());
                if (bonusApplication != null) {
                    MobEffectInstance bonusEffect = bonusApplication.apply(effect);
                    IMobEffectInstanceMixin bonusEffectMixin = (IMobEffectInstanceMixin)bonusEffect;
                    bonusEffectMixin.setSourceEntity(sourcePlayer);
                    bonusEffectMixin.setApplicationSkipped(true);
                    effectEntity.addEffect(bonusEffect);
                }
            }
        }

        if (isPlayerApplication) {
            ServerPlayer effectPlayer = (ServerPlayer)effectEntity;
            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(effectPlayer);
            Skills effectManipulator = Skills.EFFECT_MANIPULATOR;
            if (playerSkillData == null || !playerSkillData.hasSkill(effectManipulator)) return; //No further processing from here, just return

            int duration = effect.getDuration();
            if (duration != -1) {
                switch (effect.getEffect().getCategory()) {
                    case BENEFICIAL ->
                            ((MobEffectInstanceAccessor)effect).setDuration((int) Math.round(duration * ((EffectManipulatorSkill) effectManipulator.getSkill()).boostAmount(playerSkillData.getSkillRank(effectManipulator))));
                    case HARMFUL ->
                            ((MobEffectInstanceAccessor)effect).setDuration((int) Math.round(duration * ((EffectManipulatorSkill) effectManipulator.getSkill()).reductionAmount(playerSkillData.getSkillRank(effectManipulator))));
                    case NEUTRAL -> {
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void xpPickupEvent(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills lifeMending = Skills.LIFE_MENDING;
        if (!playerSkillData.hasSkill(lifeMending)) return;

        LifeMendingSkill skill = (LifeMendingSkill)lifeMending.getSkill();
        byte rank = playerSkillData.getSkillRank(lifeMending);
        double chance = skill.chanceForRank(rank);
        if (player.getRandom().nextDouble() < chance) {
            double amount = skill.amountForRank(rank);
            player.heal((float)amount);
            player.playNotifySound(SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.75F, 1.75F);
        }
    }

    @SubscribeEvent
    public static void playerMountEvent(EntityMountEvent event) {
        Entity mounted = event.getEntityBeingMounted();
        Entity mounter = event.getEntityMounting();
        if (!(mounted instanceof LivingEntity livingMounted) || !(mounter instanceof ServerPlayer player)) return;

        AttributeInstance speedAttr = livingMounted.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        speedAttr.removeModifier(EXPERT_RIDER_UUID);

        if (event.isMounting()) {
            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            Skills expertRider = Skills.EXPERT_RIDER;
            if (!playerSkillData.hasSkill(expertRider)) return;

            int mod = ((ExpertRiderSkill)expertRider.getSkill()).modForRank(playerSkillData.getSkillRank(expertRider));
            speedAttr.addTransientModifier(EXPERT_RIDER_MODS[mod]);
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase.equals(TickEvent.Phase.END)) return;

        Player player = event.player;
        ServerLevel level = (ServerLevel)player.level();

        if (level.getGameTime() % 30 != 0) return;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills diurnalExploration = Skills.DIURNAL_EXPLORATION;
        if (!playerSkillData.hasSkill(diurnalExploration)) return;

        if (level.getBrightness(LightLayer.SKY, player.blockPosition()) >= 15 && !level.dimensionType().hasFixedTime()) {
            player.addEffect(new MobEffectInstance(level.isDay() ? MobEffects.MOVEMENT_SPEED : MobEffects.REGENERATION, 60, 0, true, false, true));
        }
    }

    @SubscribeEvent
    public static void playerCriticalEvent(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !event.isVanillaCritical()) return;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills preciseStrikes = Skills.PRECISE_STRIKES;
        if (!playerSkillData.hasSkill(preciseStrikes)) return;

        PreciseStrikesSkill skill = ((PreciseStrikesSkill)preciseStrikes.getSkill());
        byte rank = playerSkillData.getSkillRank(preciseStrikes);
        int amplifier = skill.amountForRank(rank);
        int duration = skill.durationForRank(rank);
        player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_PRECISE_STRIKES.get(), duration * 20, amplifier, true, false, true));
    }

    @SubscribeEvent
    public static void arrowDenseImpact(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractArrow arrow) || !(arrow.getOwner() instanceof ServerPlayer player) || !arrow.isCritArrow()) return;

        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills denseImpact = Skills.DENSE_IMPACT;
        if (playerSkillData.hasSkill(denseImpact)) {
            DenseImpactSkill skill = ((DenseImpactSkill) denseImpact.getSkill());
            byte rank = playerSkillData.getSkillRank(denseImpact);
            if (player.getRandom().nextDouble() < skill.chanceForRank(rank)) {
                CompoundTag impactData = new CompoundTag();
                impactData.putDouble("Damage", skill.damageForRank(rank));
                impactData.putDouble("Radius", skill.radiusForRank(rank));
                impactData.putBoolean("Activated", false);
                arrow.getPersistentData().put("ImpactData", impactData);
            }
        }

        Skills quickShot = Skills.QUICK_SHOT;
        if (playerSkillData.hasSkill(quickShot) && !player.hasEffect(Registry.EFFECT_QUICK_SHOT.get())) {
            arrow.getPersistentData().putInt("QuickShot", ((QuickShotSkill)quickShot.getSkill()).shotsForRank(playerSkillData.getSkillRank(quickShot)));
        }
    }

    @SubscribeEvent
    public static void useProjectileWeapon(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        ItemStack item = event.getItem();
        if (!entity.hasEffect(Registry.EFFECT_QUICK_SHOT.get()) || !(item.getItem() instanceof ProjectileWeaponItem weaponItem)) return;

        if (!entity.level().isClientSide) {
            ServerPlayer player = (entity instanceof ServerPlayer) ? (ServerPlayer)entity : null;
            weaponItem.releaseUsing(item, entity.level(), entity, 0);
            if (player != null) {
                PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
                Skills quickShot = Skills.QUICK_SHOT;
                int cooldown = playerSkillData.hasSkill(quickShot) ? ((QuickShotSkill) quickShot.getSkill()).cooldownForRank(playerSkillData.getSkillRank(quickShot)) : 15;
                player.getCooldowns().addCooldown(weaponItem, cooldown - (int)Math.ceil(item.getEnchantmentLevel(Enchantments.QUICK_CHARGE) * 1.5));
            }

            MobEffectInstance quickShot = entity.getEffect(Registry.EFFECT_QUICK_SHOT.get());
            entity.removeEffect(Registry.EFFECT_QUICK_SHOT.get());
            int amplifier = quickShot.getAmplifier();
            int duration = quickShot.getDuration();

            if (amplifier > 0) {
                entity.addEffect(new MobEffectInstance(Registry.EFFECT_QUICK_SHOT.get(), Math.min(200, duration + 100), amplifier - 1, true, false, true));
                //if (player != null) { player.playNotifySound(SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.75F); }
            }
            else if (player != null) { player.playNotifySound(SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0F, 1.75F); }
        }
        event.setCanceled(true);
    }
}
