package net.mcreator.ars_technica.setup;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.packets.ConfigureSourceEnginePacket;
import net.mcreator.ars_technica.common.packets.CustomCooldownPacket;
import net.mcreator.ars_technica.common.packets.TicksUntilChargePacket;
import net.mcreator.ars_technica.network.ParticleEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ArsTechnicaMod.MODID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        int id = 0;
        CHANNEL.messageBuilder(ParticleEffectPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ParticleEffectPacket::encode)
                .decoder(ParticleEffectPacket::decode)
                .consumerMainThread(ParticleEffectPacket::handle)
                .add();

        CHANNEL.messageBuilder(ConfigureSourceEnginePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ConfigureSourceEnginePacket::write)
                .decoder(ConfigureSourceEnginePacket::new)
                .consumerMainThread((p, c) -> p.handle(c.get()))
                .add();

        CHANNEL.messageBuilder(TicksUntilChargePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TicksUntilChargePacket::encode)
                .decoder(TicksUntilChargePacket::decode)
                .consumerMainThread(TicksUntilChargePacket::handle)
                .add();

        CHANNEL.messageBuilder(CustomCooldownPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CustomCooldownPacket::encode)
                .decoder(CustomCooldownPacket::decode)
                .consumerMainThread(CustomCooldownPacket::handle)
                .add();
    }
}