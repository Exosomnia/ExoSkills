package com.exosomnia.exoskills.item;

import com.exosomnia.exolib.utils.ComponentUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//TODO: The whole logic surrounding these things are super messy, Rework Arcane singularities, poorly laid out.
public class ArcaneSingularityItem extends Item {

    public enum SingularityEffect {
        UNBREAKABLE_BOND(3),
        DECAY_DISSIPATION(3),
        SPECTRAL_FORM(2),
        SHOCK_ABSORPTION(3),
        THERMAL_CONDUCTIVITY(3),
        FLAWLESS_BLADE(1),
        SOUL_BURN(2),
        CRIPPLING_STRIKE(2);

        public final int descLines;

        SingularityEffect(int descLines) {
            this.descLines = descLines;
        }
    }

    private static final ImmutableMap<Enchantment, SingularityEffect> singularityEffects;
    public static final Enchantment[] singularityKeys;
    static {
        singularityEffects = ImmutableMap.of(
                Enchantments.BINDING_CURSE, SingularityEffect.UNBREAKABLE_BOND,
                Enchantments.VANISHING_CURSE, SingularityEffect.DECAY_DISSIPATION,
                Enchantments.PROJECTILE_PROTECTION, SingularityEffect.SPECTRAL_FORM,
                Enchantments.BLAST_PROTECTION, SingularityEffect.SHOCK_ABSORPTION,
                Enchantments.FIRE_PROTECTION, SingularityEffect.THERMAL_CONDUCTIVITY,
                Enchantments.SHARPNESS, SingularityEffect.FLAWLESS_BLADE,
                Enchantments.FIRE_ASPECT, SingularityEffect.SOUL_BURN,
                Enchantments.KNOCKBACK, SingularityEffect.CRIPPLING_STRIKE,
                Enchantments.FLAMING_ARROWS, SingularityEffect.SOUL_BURN,
                Enchantments.PUNCH_ARROWS, SingularityEffect.CRIPPLING_STRIKE
        );
        singularityKeys = singularityEffects.keySet().toArray(new Enchantment[0]);
    }

    public ArcaneSingularityItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @javax.annotation.Nullable Level level, List<Component> components, TooltipFlag flag) {
        if (!ArcaneSingularityItem.hasSingularityData(itemStack)) return;

        Enchantment enchantment = ArcaneSingularityItem.getSingularityEnchantment(itemStack);
        if (enchantment == null) return;

        ArcaneSingularityItem.SingularityEffect singularityEffect = ArcaneSingularityItem.getSingularityEffect(enchantment);
        if (singularityEffect == null) return;

        components.add(ComponentUtils.formatLine(I18n.get("item.exoskills.arcane_singularity.info.1", enchantment.getFullname(enchantment.getMaxLevel()).getString()),
                ComponentUtils.Styles.INFO_HEADER.getStyle(), ComponentUtils.Styles.INFO_HEADER.getStyle().withUnderlined(false), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoskills.arcane_singularity.info.2", I18n.get(String.format("singularity.exoskills.%s", singularityEffect.name().toLowerCase()))),
                ComponentUtils.Styles.INFO_HEADER.getStyle(), ComponentUtils.Styles.INFO_HEADER.getStyle().withUnderlined(false), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));

        if (Screen.hasShiftDown()) {
            for (var i = 0; i < singularityEffect.descLines; i++) {
                components.add(ComponentUtils.formatLine(I18n.get(String.format("singularity.exoskills.%1$s.desc.%2$d", singularityEffect.name().toLowerCase(), i + 1)),
                        ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
            }
        }
    }

    public static SingularityEffect getSingularityEffect(Enchantment enchantment) {
        return singularityEffects.get(enchantment);
    }

    public static Enchantment getSingularityEnchantment(ItemStack itemStack) {
        CompoundTag singularityTag = getSingularityTag(itemStack);
        if (singularityTag == null) return null;

        return ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.bySeparator(singularityTag.getString("Enchantment"), ':'));
    }

    public static boolean hasSingularityEffect(ItemStack itemStack, SingularityEffect checkedEffect) {
        CompoundTag singularityTag = getSingularityTag(itemStack);
        if (singularityTag == null) return false;

        Enchantment enchantment = getSingularityEnchantment(itemStack);
        if (enchantment == null) return false;

        SingularityEffect effect = singularityEffects.get(enchantment);
        if (effect == null) return false;

        return effect.equals(checkedEffect);
    }

    public static boolean hasSingularityData(ItemStack itemStack) {
        return getSingularityTag(itemStack) != null;
    }

    public static boolean isSingularityActive(ItemStack itemStack) {
        CompoundTag singularityTag = getSingularityTag(itemStack);
        if (singularityTag == null) return false;

        Enchantment enchantment = getSingularityEnchantment(itemStack);
        return singularityTag.getBoolean("Active") && (enchantment != null && itemStack.getEnchantmentLevel(enchantment) >= enchantment.getMaxLevel());
    }

    @Nullable
    public static CompoundTag getSingularityTag(ItemStack itemStack) {
        if (!itemStack.hasTag()) return null;

        CompoundTag itemTag = itemStack.getTag();
        Tag singularityTag = itemTag.get("SingularityData");
        if (!(singularityTag instanceof CompoundTag cSingularityTag)) return null;

        return cSingularityTag.copy();
    }
}
