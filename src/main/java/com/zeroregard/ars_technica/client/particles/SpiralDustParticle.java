package com.zeroregard.ars_technica.client.particles;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class SpiralDustParticle extends TextureSheetParticle {
    private final float radius;
    private final float speed;
    private float angle;
    public float initScale;
    private final float initialQuadSize;

    protected SpiralDustParticle(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int lifetime, SpriteSet sprite) {
        super(worldIn, x, y, z, 0, 0, 0);
        this.radius = 0.1f;
        this.speed = 0.1f;
        this.angle = (float) (Math.random() * 2 * Math.PI);
        this.quadSize = scale * 0.08f + (float)(Math.random() * 0.04);
        this.initialQuadSize = this.quadSize;
        this.hasPhysics = false;
        this.initScale = scale * 0.01f;
        this.xd = ParticleUtil.inRange(-0.01, 0.01);
        this.yd = -0.02;
        this.zd = ParticleUtil.inRange(-0.01, 0.01);

        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.setColor(r, g, b);
        this.lifetime = lifetime + (int)Math.floor(Math.random() * 50);

        this.pickSprite(sprite);
    }

    @Override
    public void tick() {
        super.tick();

        angle += speed;
        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        double x = this.x + radius * 0.5 * Math.sin(angle);
        double y = this.y + 0.025;
        double z = this.z + radius * 0.5 * Math.cos(angle);

        float progress = Math.min((float) this.age / this.lifetime, 1.0f);
        float easedProgress = applyEasingFunction(progress);
        this.quadSize = this.initialQuadSize - easedProgress * this.initialQuadSize;

        this.setPos(x, y, z);
    }

    private float applyEasingFunction(float progress) {
        if (progress <= 0) return 0;
        if (progress >= 1) return 1;

        return (float) Math.pow(2, 10 * (progress - 1));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
