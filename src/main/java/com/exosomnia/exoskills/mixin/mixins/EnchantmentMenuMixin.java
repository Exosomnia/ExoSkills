package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.item.ArcaneSingularityItem;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.ArcaneOverloadSkill;
import com.exosomnia.exoskills.skill.type.OccultApothecarySkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow
    private Container enchantSlots;

    @Shadow
    private ContainerLevelAccess access;

    @Inject(method = "clickMenuButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V", shift = At.Shift.AFTER))
    private void arcaneOverloadInject(Player player, int index, CallbackInfoReturnable<Boolean> cir) {
        int chanceMod = index + 1;
        access.execute(((level, blockPos) -> {
            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            Skills arcaneOverload = Skills.ARCANE_OVERLOAD;
            if (playerSkillData.hasSkill(arcaneOverload) && level.random.nextDouble() < chanceMod * ((ArcaneOverloadSkill)arcaneOverload.getSkill()).chanceForRank(playerSkillData.getSkillRank(arcaneOverload))) {
                ItemStack singularity = new ItemStack(Registry.ITEM_ARCANE_SINGULARITY.get(), 1);
                CompoundTag baseTag = singularity.getOrCreateTag();
                baseTag.put("SingularityData", new CompoundTag());
                CompoundTag singularityData = (CompoundTag)baseTag.get("SingularityData");
                Enchantment enchantment = ArcaneSingularityItem.singularityKeys[level.random.nextInt(ArcaneSingularityItem.singularityKeys.length)];
                ResourceLocation resourceLocation = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                if (resourceLocation != null) {
                    level.playSound(null, blockPos, SoundEvents.TRIDENT_RETURN, SoundSource.BLOCKS, 1.0F, 0.5F);
                    singularityData.putString("Enchantment", resourceLocation.toString());
                    singularityData.putBoolean("Active", false);
                    if (!player.getInventory().add(singularity.copy())) {
                        player.drop(singularity, false);
                    }
                }
            }
        }));
    }

    @ModifyArg(method = "getEnchantmentList", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;IZ)Ljava/util/List;"), index = 3)
    private boolean fateTreasureEnchants(boolean treasure) {
        return this.enchantSlots.getItem(1).is(Registry.ITEM_HORIZON_OF_FATE.get());
    }
}
