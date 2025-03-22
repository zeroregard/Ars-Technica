package com.zeroregard.ars_technica.network;


import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ArsTechnica.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkManager {
    public static final String PROTOCOL_VERSION = "1";

    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar reg = event.registrar(PROTOCOL_VERSION);
        reg.playToClient(ParticleEffectPacket.TYPE, ParticleEffectPacket.CODEC, NetworkManager::handle);
        reg.playToServer(TicksUntilChargePacket.TYPE, TicksUntilChargePacket.CODEC, NetworkManager::handle);
        reg.playToServer(CustomCooldownPacket.TYPE, CustomCooldownPacket.CODEC, NetworkManager::handle);

    }

    private static <T extends AbstractPacket> void handle(T message, IPayloadContext ctx) {
        if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
            handleServer(message, ctx);
        } else {
            ClientMessageHandler.handleClient(message, ctx);
        }
    }

    private static <T extends AbstractPacket> void handleServer(T message, IPayloadContext ctx) {
        MinecraftServer server = ctx.player().getServer();
        message.onServerReceived(server, (ServerPlayer) ctx.player());
    }

    private static class ClientMessageHandler {

        public static <T extends AbstractPacket> void handleClient(T message, IPayloadContext ctx) {
            Minecraft minecraft = Minecraft.getInstance();
            message.onClientReceived(minecraft, minecraft.player);
        }
    }

}