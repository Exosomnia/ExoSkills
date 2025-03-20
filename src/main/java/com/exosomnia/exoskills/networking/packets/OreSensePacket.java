package com.exosomnia.exoskills.networking.packets;

import com.exosomnia.exoskills.ExoSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class OreSensePacket {

    private Block block;
    private byte rank;

    public OreSensePacket(Block block, byte rank) {
        this.block = block;
        this.rank = rank;
    }

    public OreSensePacket(FriendlyByteBuf buffer) {
        block = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
        block = block == null ? Blocks.AIR : block;
        rank = buffer.readByte();
    }

    public static void encode(OreSensePacket packet, FriendlyByteBuf buffer) {
        ResourceLocation blockResource = ForgeRegistries.BLOCKS.getKey(packet.block);
        buffer.writeResourceLocation(blockResource == null ? ResourceLocation.bySeparator("minecraft:air", ':') : blockResource);
        buffer.writeByte(packet.rank);
    }

    public static void handle(OreSensePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {
                ExoSkills.RENDER_MANAGER.displayOres(packet.block, packet.rank);
            }
        });
        context.get().setPacketHandled(true);
    }
}
