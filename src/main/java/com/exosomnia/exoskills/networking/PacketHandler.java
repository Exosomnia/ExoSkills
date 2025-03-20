package com.exosomnia.exoskills.networking;

import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.exosomnia.exolib.networking.packets.TagUpdatePacket;
import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.networking.packets.LuckyEssencePacket;
import com.exosomnia.exoskills.networking.packets.OreSensePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(ExoSkills.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        INSTANCE.registerMessage(id++, OreSensePacket.class, OreSensePacket::encode, OreSensePacket::new, OreSensePacket::handle);
        INSTANCE.registerMessage(id++, LuckyEssencePacket.class, LuckyEssencePacket::encode, LuckyEssencePacket::new, LuckyEssencePacket::handle);
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
