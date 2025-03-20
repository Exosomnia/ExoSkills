package com.exosomnia.exoskills.client.rendering.events;

import com.exosomnia.exolib.utils.ComponentUtils;
import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.item.ArcaneSingularityItem;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ExoSkills.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderTooltipHandler {

    @SubscribeEvent
    public static void tooltipRenderEvent(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (stack.isDamageableItem() && (stack.hasTag() && stack.getTag().getBoolean("Reinforcement"))) {
            List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
            elements.add(1, Either.left(ComponentUtils.formatLine(I18n.get("item.modifiers.reinforcement"),
                    ComponentUtils.Styles.BLANK.getStyle().withColor(ChatFormatting.DARK_GRAY).withItalic(true))));
        }
        else if (stack.getItem() instanceof BlockItem && (stack.hasTag() && stack.getTag().getBoolean("Unplaceable"))) {
            List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
            elements.add(1, Either.left(ComponentUtils.formatLine(I18n.get("item.modifiers.unplaceable"),
                    ComponentUtils.Styles.BLANK.getStyle().withColor(ChatFormatting.DARK_GRAY).withItalic(true))));
        }
    }

    public static void tooltipAppendAfterEnchants(List<Component> components, ItemStack itemStack) {
        if (!ArcaneSingularityItem.hasSingularityData(itemStack) || itemStack.is(Registry.ITEM_ARCANE_SINGULARITY.get())) return;

        Enchantment enchantment = ArcaneSingularityItem.getSingularityEnchantment(itemStack);
        if (enchantment == null) return;

        ArcaneSingularityItem.SingularityEffect singularityEffect = ArcaneSingularityItem.getSingularityEffect(enchantment);
        if (singularityEffect == null) return;

        components.add(ComponentUtils.formatLine(I18n.get(String.format("singularity.exoskills.%1$s", singularityEffect.name().toLowerCase())),
                ComponentUtils.Styles.BLANK.getStyle().withColor(0x912AEB)));

        if (Screen.hasShiftDown()) {
            for (var i = 0; i < singularityEffect.descLines; i++) {
                components.add(ComponentUtils.formatLine(I18n.get(String.format("singularity.exoskills.%1$s.desc.%2$d", singularityEffect.name().toLowerCase(), i + 1)),
                        ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
            }
        }
    }
}
