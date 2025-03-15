package com.zeroregard.ars_technica.client.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SpiralDustParticleType extends ParticleType<SpiralDustParticleTypeData> {
    public SpiralDustParticleType() {
        super(false);
    }

    @Override
    public MapCodec<SpiralDustParticleTypeData> codec() {
        return SpiralDustParticleTypeData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SpiralDustParticleTypeData> streamCodec() {
        return SpiralDustParticleTypeData.STREAM_CODEC;
    }

}