package net.mcreator.ars_technica.common.kinetics;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.events.ModParticles;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.network.ParticleEffectPacket;
import net.mcreator.ars_technica.setup.NetworkHandler;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class WhirlCurrent {

    private final WhirlEntity source;
    private AABB bounds;
    private List<ItemEntity> affectedEntities;
    private float radius;
    private int tickCount = 0;

    public WhirlCurrent(WhirlEntity source) {
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
        List<ServerPlayer> nearbyPlayers = getNearbyPlayers(world);
        if (tickCount % 4 == 0) {
            sendWhirlParticles(nearbyPlayers, source.getProcessor());
        }
        for (Iterator<ItemEntity> iterator = affectedEntities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            if (!entity.isAlive() || !entity.getBoundingBox().intersects(bounds)) {
                iterator.remove();
                continue;
            }

            Vec3 direction = source.position().subtract(entity.position()).normalize();
            double distance = entity.position().distanceTo(source.position());

            Vec3 motion = entity.getDeltaMovement();
            Vec3 tangentialMotion = new Vec3(-direction.z, 0, direction.x).scale(0.05);
            Vec3 pull = direction.scale(0.05 * (Math.sqrt(radius) - distance));

            entity.setDeltaMovement(motion.add(tangentialMotion).add(pull));
            entity.fallDistance = 0;

            FanProcessingType processingType = source.getProcessor();

            entity.hurtMarked = true;

            if (processingType == AllFanProcessingTypes.NONE)
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
                        sendProcessingParticles(nearbyPlayers, itemEntity, processingType); // TODO: these should be more 'explosive' for this
                    }
                    sendProcessingParticles(nearbyPlayers, itemEntity, processingType);
                }
            }
        }
    }

    private void sendProcessingSound(Vec3 itemPos, FanProcessingType processingType) {
        SoundEvent event = ArsTechnicaModSounds.WHIRL_PROCESS_AFFECT_FIRE.get();
        if (processingType == AllFanProcessingTypes.SPLASHING) {
            event = ArsTechnicaModSounds.WHIRL_PROCESS_AFFECT_WATER.get();
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

    private List<ServerPlayer> getNearbyPlayers(Level world) {
        double playerRadius = 50;
        AABB playerBounds = new AABB(source.position().subtract(playerRadius, playerRadius, playerRadius),
                source.position().add(playerRadius, playerRadius, playerRadius));
        return world.getEntitiesOfClass(ServerPlayer.class, playerBounds);
    }

    private void sendWhirlParticles(List<ServerPlayer> players, FanProcessingType processingType) {
        if (processingType == AllFanProcessingTypes.NONE) {
            return;
        }
        ParticleColor color = ParticleColor.WHITE;
        if(processingType == AllFanProcessingTypes.BLASTING || processingType == AllFanProcessingTypes.HAUNTING || processingType == AllFanProcessingTypes.SMOKING) {
            color = new ParticleColor(32, 32, 32);
        }
        for (ServerPlayer player : players) {
            ParticleEffectPacket packet = new ParticleEffectPacket(source.getPosition(1.0f), ModParticles.SPIRAL_DUST_TYPE.get(), color);
            NetworkHandler.CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    private void sendProcessingParticles(List<ServerPlayer> players, ItemEntity itemEntity, FanProcessingType processingType) {
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

        for (ServerPlayer player : players) {
            ParticleEffectPacket packet = new ParticleEffectPacket(itemPos, particleType);
            NetworkHandler.CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
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
