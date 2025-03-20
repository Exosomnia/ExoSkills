package com.exosomnia.exoskills.item;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.utils.ComponentUtils;
import com.exosomnia.exoskills.action.HorizonOfFateAction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class HorizonOfFateItem extends Item {

    public HorizonOfFateItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @javax.annotation.Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoskills.horizon_of_fate.info.1"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoskills.horizon_of_fate.info.2"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoskills.horizon_of_fate.info.3"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) { return InteractionResultHolder.consume(itemStack); }

        player.sendSystemMessage(Component.translatable("item.exoskills.horizon_of_fate.use.rewarded").withStyle(ChatFormatting.GOLD));
        player.playNotifySound(SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 0.5F);
        ExoLib.SERVER_SCHEDULE_MANAGER.scheduleAction(new HorizonOfFateAction((ServerPlayer)player, level.random.nextInt(1395)), 1);

        itemStack.shrink(1);
        return InteractionResultHolder.consume(itemStack);
    }
}
