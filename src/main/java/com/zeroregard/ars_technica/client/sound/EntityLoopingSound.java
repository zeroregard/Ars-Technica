package com.zeroregard.ars_technica.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;


public class EntityLoopingSound extends AbstractTickableSoundInstance {

    private final Entity entity;
    private final double maxDistance = 16;
    private final float maxVolume;
    private final double maxDistanceSquared = maxDistance * maxDistance;

    public EntityLoopingSound(Entity entity, SoundEvent sound, float volume, float pitch) {
        super(sound, SoundSource.AMBIENT, RandomSource.create());
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.maxVolume = volume;
        this.pitch = pitch;
        this.attenuation = Attenuation.LINEAR;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void tick() {
        if (!entity.isAlive()) {
            this.stop();
            return;
        }

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();

        // Check distance to player
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            double distanceSquared = minecraft.player.distanceToSqr(this.x, this.y, this.z);
            if (distanceSquared <= maxDistanceSquared) {
                float distanceFactor = (float) Math.sqrt(distanceSquared) / (float) maxDistance;
                this.volume = Math.max(0.0f, 1.0f - distanceFactor) * maxVolume;
            }
        }
    }
}
