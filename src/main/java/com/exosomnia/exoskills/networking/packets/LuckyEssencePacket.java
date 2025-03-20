package com.exosomnia.exoskills.networking.packets;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.rendering.RenderingManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class LuckyEssencePacket {

    private BlockPos[] positions;
    private int color;

    public LuckyEssencePacket(BlockPos[] positions, int color) {
        this.positions = positions;
        this.color = color;
    }

    public LuckyEssencePacket(FriendlyByteBuf buffer) {
        int count = buffer.readInt();
        positions = new BlockPos[count];
        for (var i = 0; i < count; i++) {
            positions[i] = buffer.readBlockPos();
        }
        color = buffer.readInt();
    }

    public static void encode(LuckyEssencePacket packet, FriendlyByteBuf buffer) {
        int count = packet.positions.length;
        buffer.writeInt(count);
        for (var i = 0; i < count; i++) {
            buffer.writeBlockPos(packet.positions[i]);
        }
        buffer.writeInt(packet.color);
    }

    public static void handle(LuckyEssencePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {
                ExoSkills.RENDER_MANAGER.displayPositions(RenderingManager.HighlightType.DEFINED_CONTAINERS, packet.positions, packet.color);
            }
        });
        context.get().setPacketHandled(true);
    }
}
