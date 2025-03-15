package com.zeroregard.ars_technica.network;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.client.particles.SpiralDustParticleTypeData;
import com.zeroregard.ars_technica.registry.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ParticleEffectPacket extends AbstractPacket {

    private final Vec3 position;
    private final ParticleType<?> particleType;
    private final ParticleColor particleColor;

    public ParticleEffectPacket(Vec3 position, ParticleType<?> particleType, ParticleColor particleColor) {
        this.position = position;
        this.particleType = particleType;
        this.particleColor = particleColor;
    }

    public static void encode(ParticleEffectPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.position.x);
        buf.writeDouble(msg.position.y);
        buf.writeDouble(msg.position.z);
        buf.writeFloat(msg.particleColor.getRed());
        buf.writeFloat(msg.particleColor.getGreen());
        buf.writeFloat(msg.particleColor.getBlue());
        buf.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(msg.particleType)));
    }

    public static ParticleEffectPacket decode(FriendlyByteBuf buf) {
        Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int r = Math.round(buf.readFloat() * 255);
        int g = Math.round(buf.readFloat() * 255);
        int b = Math.round(buf.readFloat() * 255);
        ParticleColor color = new ParticleColor(r, g, b);
        ResourceLocation particleId = buf.readResourceLocation();
        ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(particleId);

        return new ParticleEffectPacket(position, particleType, color);
    }

    public static void send(@Nonnull Level level, @Nonnull ParticleColor particleColor, @Nonnull ParticleType<?> particleType, @Nonnull Vec3 position) {
        if (level instanceof ServerLevel serverLevel) {
            ParticleEffectPacket packet = new ParticleEffectPacket(position, particleType, particleColor);
            Networking.sendToNearbyClient(serverLevel, BlockPos.containing(position), packet);
        }
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel clientWorld = mc.level;
        if (clientWorld != null && this.particleType != null) {
            if (this.particleType == ParticleTypes.DUST) {
                DustParticleOptions dustOptions = new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
                clientWorld.addParticle(dustOptions, this.position.x, this.position.y, this.position.z, 0, 0, 0);
            } else if (this.particleType == ParticleTypes.SOUL_FIRE_FLAME) {
                clientWorld.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.position.x, this.position.y + 0.45, this.position.z, 0.0, 0.0, 0.0);
            } else if (this.particleType == ParticleTypes.SMOKE) {
                clientWorld.addParticle(ParticleTypes.SMOKE, this.position.x, this.position.y + 0.25, this.position.z, 0.0, 0.0625, 0.0);
            } else if (this.particleType == ParticleTypes.POOF) {
                clientWorld.addParticle(ParticleTypes.POOF, this.position.x, this.position.y + 0.25, this.position.z, 0.0, 0.0625, 0.0);
            }
            else if (this.particleType == ParticleRegistry.SPIRAL_DUST_TYPE.get()) {
                SpiralDustParticleTypeData data = new SpiralDustParticleTypeData(ParticleRegistry.SPIRAL_DUST_TYPE.get(), this.particleColor, false);
                clientWorld.addParticle(data, this.position.x, this.position.y + 0.25, this.position.z, 0, 0, 0);
            }
        }
    }

    public static final CustomPacketPayload.Type<ParticleEffectPacket> TYPE = new CustomPacketPayload.Type<>(ArsTechnica.prefix("particle_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleEffectPacket> CODEC = StreamCodec.ofMember(ParticleEffectPacket::encode, ParticleEffectPacket::decode);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
