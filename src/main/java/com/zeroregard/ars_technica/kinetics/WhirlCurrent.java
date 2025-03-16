package com.zeroregard.ars_technica.kinetics;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.zeroregard.ars_technica.entity.ArcaneWhirlEntity;
import com.zeroregard.ars_technica.network.ParticleEffectPacket;
import com.zeroregard.ars_technica.registry.ParticleRegistry;
import com.zeroregard.ars_technica.registry.SoundRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class WhirlCurrent {

    private final ArcaneWhirlEntity source;
    private AABB bounds;
    private List<ItemEntity> affectedEntities;
    private float radius;
    private int tickCount = 0;
    private double tangentialFactor = 0.25;
    private double pullFactor = 3.0;

    public WhirlCurrent(ArcaneWhirlEntity source) {
        this.source = source;
        this.radius = source.getRadius();

        Vec3 centerPos = source.getPosition(1);
        bounds = new AABB(centerPos.subtract(radius, radius, radius),
                centerPos.add(radius, radius, radius));
    }

    public void tick(SpellResolver whirlOwner) {
        tickCount++;
        Level world = source.getLevel();
        if (world == null) return;
        tickAffectedEntities(world, whirlOwner);
    }

    protected void tickAffectedEntities(Level world, SpellResolver whirlOwner) {
        affectedEntities = world.getEntitiesOfClass(ItemEntity.class, bounds);
        if (tickCount % 4 == 0) {
            sendWhirlParticles(world, source.getProcessor());
        }
        for (Iterator<ItemEntity> iterator = affectedEntities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            if (!entity.isAlive() || !entity.getBoundingBox().intersects(bounds)) {
                iterator.remove();
                continue;
            }

            moveItem(entity);

            FanProcessingType processingType = source.getProcessor();

            entity.hurtMarked = true;

            if (processingType == null)
                continue;

            if (entity instanceof ItemEntity itemEntity) {
                if (WhirlProcessing.canProcess(itemEntity, processingType)) {
                    Vec3 itemPosition = itemEntity.getPosition(1.0f);
                    if (WhirlProcessing.applyProcessing(itemEntity, processingType, world, whirlOwner)) {
                        sendProcessingFinishedSound(itemPosition, processingType);
                    }
                    if (tickCount % 8 == 0) {
                        // Play a sound and send particles again
                        sendProcessingSound(itemPosition, processingType);
                        sendProcessingParticles(world, itemEntity, processingType); // TODO: these should be more 'explosive' for this
                    }
                    sendProcessingParticles(world, itemEntity, processingType);
                }
            }
        }
    }

    private void moveItem(Entity entity) {
        Vec3 direction = source.position().subtract(entity.position()).normalize();
        double distance = entity.position().distanceTo(source.position());

        // I'm really sorry about my lack of physics knowledge, have some spaghetti. Do not touch the spaghetti, no one knows how it really works
        Vec3 motion = entity.getDeltaMovement();
        double heightDifference = Math.abs(entity.position().y - source.position().y);
        double heightFactor = Math.max(0, 1 - (heightDifference));
        Vec3 tangentialMotion = new Vec3(-direction.z, 0, direction.x).scale(tangentialFactor * heightFactor * (radius / 3) * source.getScaledSpeed() * 0.5f );
        Vec3 pull = direction.scale(pullFactor * (Math.sqrt(radius) - distance * 1.5) * heightFactor * (1/radius) * source.getScaledSpeed() * 0.5f);
        if(pull.length() > 0.5) {
            pull = pull.normalize().scale(0.1 * radius);
        }
        entity.setDeltaMovement(motion.add(tangentialMotion).subtract(pull));
        entity.fallDistance = 0;
    }

    private void sendProcessingSound(Vec3 itemPos, FanProcessingType processingType) {
        SoundEvent event = SoundRegistry.WHIRL_PROCESS_AFFECT_FIRE.get();
        if (processingType == AllFanProcessingTypes.SPLASHING) {
            event = SoundRegistry.WHIRL_PROCESS_AFFECT_WATER.get();
        }
        sendSoundEvent(itemPos, event);
    }

    private void sendProcessingFinishedSound(Vec3 itemPos, FanProcessingType processingType) {
        SoundEvent event = SoundEvents.FIRE_EXTINGUISH;
        if (processingType == AllFanProcessingTypes.SPLASHING) {
            event = SoundEvents.PLAYER_SPLASH;
        }
        sendSoundEvent(itemPos, event);
    }

    private void sendSoundEvent(Vec3 pos, SoundEvent event) {
        if(event != null) {
            source.getLevel().playSound(null, pos.x, pos.y, pos.z, event, SoundSource.AMBIENT, 0.25f, 1.0f);
        }
    }

    private void sendWhirlParticles(Level world, FanProcessingType processingType) {
        if (processingType == null) {
            return;
        }
        ParticleColor color = ParticleColor.WHITE;
        if(processingType == AllFanProcessingTypes.BLASTING || processingType == AllFanProcessingTypes.HAUNTING || processingType == AllFanProcessingTypes.SMOKING) {
            color = new ParticleColor(32, 32, 32);
        }
        ParticleEffectPacket.send(world, ParticleColor.fromInt(color.getColor()),  ParticleRegistry.SPIRAL_DUST_TYPE.get(), source.getPosition(1.0f));

    }

    private void sendProcessingParticles(Level world, ItemEntity itemEntity, FanProcessingType processingType) {
        Vec3 itemPos = itemEntity.position();
        ParticleType<?> particleType;

        if(processingType == AllFanProcessingTypes.BLASTING) {
            particleType = ParticleTypes.SMOKE;
        }
        else if(processingType == AllFanProcessingTypes.HAUNTING) {
            particleType = ParticleTypes.SOUL_FIRE_FLAME;
        }
        else if(processingType == AllFanProcessingTypes.SMOKING) {
            particleType = ParticleTypes.POOF;
        }
        else {
            particleType = ParticleTypes.DUST;
        }

        ParticleEffectPacket.send(world, particleType, itemPos);
    }


    public void stopAffectedItems() {
        if (affectedEntities == null) {
            return;
        }
        for (ItemEntity item : affectedEntities) {
            item.setDeltaMovement(Vec3.ZERO);
            item.hurtMarked = true;
        }
        affectedEntities.clear();
    }

    private static UUID getProcessingId(Entity entity) {
        CompoundTag nbt = entity.getPersistentData();
        if (nbt.contains("CreateData")) {
            CompoundTag createData = nbt.getCompound("CreateData");
            if (createData.contains("WhirlwindId")) {
                return createData.getUUID("WhirlwindId");
            }
        }
        return null;
    }


}
