package com.zeroregard.ars_technica.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class SpiralDustParticleProvider implements ParticleProvider<SpiralDustParticleTypeData> {
    private final SpriteSet spriteSet;

    public SpiralDustParticleProvider(SpriteSet sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle createParticle(SpiralDustParticleTypeData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new SpiralDustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(),
                1.0f,
                20, this.spriteSet);
    }
}