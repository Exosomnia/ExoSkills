package com.exosomnia.exoskills.item;

import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.mixin.mixins.RandomizableContainerBlockEntityAccessor;
import com.exosomnia.exoskills.networking.PacketHandler;
import com.exosomnia.exoskills.networking.packets.LuckyEssencePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuckyEssenceItem extends Item {
    public LuckyEssenceItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) { return InteractionResultHolder.consume(itemStack); }

        player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 0.75F);

        BlockPos playerPos = player.blockPosition();
        int currentChunkX = SectionPos.blockToSectionCoord(playerPos.getX());
        int currentChunkZ = SectionPos.blockToSectionCoord(playerPos.getZ());

        List<BlockPos> foundBlocks = new ArrayList<>();
        for (int dx = currentChunkX - 1; dx <= currentChunkX + 1; dx++) {
            for (int dz = currentChunkZ - 1; dz <= currentChunkZ + 1; dz++) {
                for (Map.Entry<BlockPos, BlockEntity> entry : level.getChunk(dx, dz).getBlockEntities().entrySet()) {
                    if (isVaildLootContainer(player, entry.getValue())) {
                        foundBlocks.add(entry.getKey());
                    }
                }
            }
        }
        PacketHandler.sendToPlayer(new LuckyEssencePacket(foundBlocks.toArray(new BlockPos[0]), 0xDED362), (ServerPlayer)player);

        player.getCooldowns().addCooldown(this, 100);
        itemStack.shrink(1);
        return InteractionResultHolder.consume(itemStack);
    }

    private boolean isVaildLootContainer(Player player, BlockEntity blockEntity) {
        if (!(blockEntity instanceof RandomizableContainerBlockEntity randomContainerEntity)) return false;

        boolean lootTablePresent = ((RandomizableContainerBlockEntityAccessor)randomContainerEntity).getLootTable() != null;
        boolean lootrValid = true;
        if (Registry.isLoaded(Registry.Integrations.LOOTR) && (randomContainerEntity instanceof ILootBlockEntity lootrBlockEntity)) {
            lootrValid = !lootrBlockEntity.getOpeners().contains(player.getUUID());
        }
        return lootTablePresent && lootrValid;
    }
}
