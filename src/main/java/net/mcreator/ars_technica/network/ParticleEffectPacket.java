package net.mcreator.ars_technica.network;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ParticleEffectPacket {
    private final Vec3 position;
    private final ParticleType<?> particleType;

    public ParticleEffectPacket(Vec3 position, ParticleType<?> particleType) {
        this.position = position;
        this.particleType = particleType;
    }

    public static void encode(ParticleEffectPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.position.x);
        buf.writeDouble(packet.position.y);
        buf.writeDouble(packet.position.z);
        buf.writeResourceLocation(ForgeRegistries.PARTICLE_TYPES.getKey(packet.particleType));
    }

    public static ParticleEffectPacket decode(FriendlyByteBuf buf) {
        Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        ResourceLocation particleId = buf.readResourceLocation();
        ParticleType<?> particleType = ForgeRegistries.PARTICLE_TYPES.getValue(particleId);
        return new ParticleEffectPacket(position, particleType);
    }

    public static void handle(ParticleEffectPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().execute(() -> {
                Level clientWorld = Minecraft.getInstance().level;
                if (clientWorld != null && packet.particleType != null) {
                    if (packet.particleType == ParticleTypes.DUST) {
                        DustParticleOptions dustOptions = new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
                        clientWorld.addParticle(dustOptions, packet.position.x, packet.position.y, packet.position.z, 0, 0, 0);
                    }
                    else if (packet.particleType == ParticleTypes.SOUL_FIRE_FLAME) {
                        clientWorld.addParticle(ParticleTypes.SOUL_FIRE_FLAME, packet.position.x, packet.position.y + 0.44999998807907104, packet.position.z, 0.0, 0.0, 0.0);
                    }
                    else if (packet.particleType == ParticleTypes.SMOKE) {
                        clientWorld.addParticle(ParticleTypes.SMOKE, packet.position.x, packet.position.y + 0.25, packet.position.z, 0.0, 0.0625, 0.0);
                    }
                    else if (packet.particleType == ParticleTypes.POOF) {
                        clientWorld.addParticle(ParticleTypes.POOF, packet.position.x, packet.position.y + 0.25, packet.position.z, 0.0, 0.0625, 0.0);
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
