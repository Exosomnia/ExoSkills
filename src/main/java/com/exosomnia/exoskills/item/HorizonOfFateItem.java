package com.exosomnia.exoskills.item;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.utils.ComponentUtils;
import com.exosomnia.exoskills.action.HorizonOfFateAction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class HorizonOfFateItem extends Item {

    private static final ResourceLocation BONUS_LOOT_TABLE = ResourceLocation.fromNamespaceAndPath("exoskills", "gameplay/horizon_bonus");

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

        int multiplier = 1 + (int)(level.random.nextInt(5) * 0.25);
        ExoLib.SERVER_SCHEDULE_MANAGER.scheduleAction(new HorizonOfFateAction((ServerPlayer)player, (3 + level.random.nextInt(9)) * multiplier), 1);
        if (multiplier >= 2) {
            ServerLevel serverLevel = (ServerLevel)level;
            ((ServerLevel)level).getServer().getLootData().getLootTable(BONUS_LOOT_TABLE).getRandomItems(new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, player).withParameter(LootContextParams.ORIGIN, player.position())
                    .withLuck(player.getLuck()).create(LootContextParamSets.GIFT)).forEach(loot -> {
                if (!player.getInventory().add(loot.copy())) {
                    player.drop(loot, false);
                }
            });
        }

        itemStack.shrink(1);
        return InteractionResultHolder.consume(itemStack);
    }
}
