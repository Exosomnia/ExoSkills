package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.client.rendering.events.RenderTooltipHandler;
import com.exosomnia.exoskills.item.ArcaneSingularityItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    private void reinforcementCheck(CallbackInfoReturnable<Integer> cir) {
        ItemStack thisStack = (ItemStack)(Object)this;
        if (thisStack.hasTag() && thisStack.getTag().getBoolean("Reinforcement")) {
            cir.setReturnValue((int)(cir.getReturnValue() * 1.2));
        }
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void singularityTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        ItemStack thisStack = (ItemStack)(Object)this;
        RenderTooltipHandler.tooltipAppendAfterEnchants(list, thisStack);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I", shift = At.Shift.BEFORE), cancellable = true)
    private void singularityDurability(int amount, RandomSource random, @Nullable ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack thisStack = (ItemStack)(Object)this;
        if (!thisStack.isDamaged() && ArcaneSingularityItem.hasSingularityEffect(thisStack, ArcaneSingularityItem.SingularityEffect.DECAY_DISSIPATION) &&
            ArcaneSingularityItem.isSingularityActive(thisStack)) {
                cir.setReturnValue(false);
        }
    }
}
