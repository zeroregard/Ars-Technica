package com.zeroregard.ars_technica.client.particles;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zeroregard.ars_technica.registry.ParticleRegistry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;


public class SpiralDustParticleTypeData implements ParticleOptions {

    protected ParticleType<? extends SpiralDustParticleTypeData> type;
    public static final MapCodec<SpiralDustParticleTypeData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
                    Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
                    Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
                    Codec.BOOL.fieldOf("disableDepthTest").forGetter(d -> d.disableDepthTest),
                    Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
                    Codec.FLOAT.fieldOf("alpha").forGetter(d -> d.alpha),
                    Codec.INT.fieldOf("age").forGetter(d -> d.age)
            )
            .apply(instance, SpiralDustParticleTypeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpiralDustParticleTypeData> STREAM_CODEC = StreamCodec.of(
            SpiralDustParticleTypeData::toNetwork, SpiralDustParticleTypeData::fromNetwork
    );

    public ParticleColor color;
    public boolean disableDepthTest;
    public float size = .25f;
    public float alpha = 1.0f;
    public int age = 36;

    public SpiralDustParticleTypeData(float r, float g, float b, boolean disableDepthTest, float size, float alpha, int age) {
        this(ParticleRegistry.SPIRAL_DUST_TYPE.get(), new ParticleColor(r, g, b), disableDepthTest, size, alpha, age);
    }

    public SpiralDustParticleTypeData(ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        this(ParticleRegistry.SPIRAL_DUST_TYPE.get(), color, disableDepthTest, size, alpha, age);
    }

    public SpiralDustParticleTypeData(ParticleType<? extends SpiralDustParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest) {
        this(particleTypeData, color, disableDepthTest, 0.1f, 1.0f, 80);
    }

    public SpiralDustParticleTypeData(ParticleType<? extends SpiralDustParticleTypeData> particleTypeData, ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        this.type = particleTypeData;
        this.color = color;
        this.disableDepthTest = disableDepthTest;
        this.size = size;
        this.alpha = alpha;
        this.age = age;
    }


    @Override
    public ParticleType<? extends SpiralDustParticleTypeData> getType() {
        return type;
    }

    public static void toNetwork(RegistryFriendlyByteBuf buf, SpiralDustParticleTypeData data) {
        buf.writeFloat(data.color.getRed());
        buf.writeFloat(data.color.getGreen());
        buf.writeFloat(data.color.getBlue());
        buf.writeBoolean(data.disableDepthTest);
        buf.writeFloat(data.size);
        buf.writeFloat(data.alpha);
        buf.writeInt(data.age);
    }

    public static SpiralDustParticleTypeData fromNetwork(RegistryFriendlyByteBuf buffer) {
        float r = buffer.readFloat();
        float g = buffer.readFloat();
        float b = buffer.readFloat();
        boolean disableDepthTest = buffer.readBoolean();
        float size = buffer.readFloat();
        float alpha = buffer.readFloat();
        int age = buffer.readInt();
        return new SpiralDustParticleTypeData(r, g, b, disableDepthTest, size, alpha, age);
    }
}
