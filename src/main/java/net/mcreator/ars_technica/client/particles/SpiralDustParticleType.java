package net.mcreator.ars_technica.client.particles;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class SpiralDustParticleType extends ParticleType<SpiralDustParticleTypeData> {
    public SpiralDustParticleType() {
        super(false, SpiralDustParticleTypeData.DESERIALIZER);
    }

    @Override
    public Codec<SpiralDustParticleTypeData> codec() {
        return SpiralDustParticleTypeData.CODEC;
    }
}