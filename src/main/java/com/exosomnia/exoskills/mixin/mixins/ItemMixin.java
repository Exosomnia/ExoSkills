package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getUseDuration(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"), cancellable = true)
    private void getUseDurationMixin(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        Integer value = cir.getReturnValue();
        if (value == 0) return;

        if (itemStack.getEnchantmentLevel(Registry.ENCHANTMENT_MASTERWORK.get()) > 0 ||
                itemStack.getEnchantmentLevel(Registry.ENCHANTMENT_HIGH_QUALITY.get()) > 0) {
            cir.setReturnValue((int)Math.ceil(value * 0.8));
        }
    }
}
