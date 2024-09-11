package net.mcreator.ars_technica.common.kinetics;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.mcreator.ars_technica.network.ParticleEffectPacket;
import net.mcreator.ars_technica.setup.NetworkHandler;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
    private double radius;
    private int tickCount = 0;

    public WhirlCurrent(WhirlEntity source) {
        this.source = source;
        this.radius = source.getRadius();

        Vec3 centerPos = source.getPosition(1);
        bounds = new AABB(centerPos.subtract(radius, radius, radius),
                centerPos.add(radius, radius, radius));
    }

    public void tick() {
        tickCount++;
        Level world = source.getLevel();
        if (world == null) return;
        tickAffectedEntities(world);
        if (tickCount % 3 == 0) {
            Vec3 pos = source.getPosition(1.0f);
            // particles go here
        }
    }


    protected void tickAffectedEntities(Level world) {
        affectedEntities = world.getEntitiesOfClass(ItemEntity.class, bounds);
        List<ServerPlayer> nearbyPlayers = getNearbyPlayers(world);
        for (Iterator<ItemEntity> iterator = affectedEntities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            if (!entity.isAlive() || !entity.getBoundingBox().intersects(bounds)) { // || !isItemBeingProcessedByThis(entity)) {
                iterator.remove();
                continue;
            }

            Vec3 direction = source.position().subtract(entity.position()).normalize();
            double distance = entity.position().distanceTo(source.position());

            Vec3 motion = entity.getDeltaMovement();
            Vec3 tangentialMotion = new Vec3(-direction.z, 0, direction.x).scale(0.05);
            Vec3 pull = direction.scale(0.03 * (radius - distance));

            entity.setDeltaMovement(motion.add(tangentialMotion).add(pull));
            entity.fallDistance = 0;

            FanProcessingType processingType = source.getProcessor();

            entity.hurtMarked = true;

            if (processingType == AllFanProcessingTypes.NONE)
                continue;

            if (entity instanceof ItemEntity itemEntity) {
                if (FanProcessing.canProcess(itemEntity, processingType)) {
                    FanProcessing.applyProcessing(itemEntity, processingType);
                    if (tickCount % 3 == 0) {
                        sendParticlesToNearbyPlayers(nearbyPlayers, itemEntity, processingType);
                    }
                }
            }
        }
    }

    private List<ServerPlayer> getNearbyPlayers(Level world) {
        double playerRadius = 50;
        AABB playerBounds = new AABB(source.position().subtract(playerRadius, playerRadius, playerRadius),
                source.position().add(playerRadius, playerRadius, playerRadius));
        return world.getEntitiesOfClass(ServerPlayer.class, playerBounds);
    }

    private void sendParticlesToNearbyPlayers(List<ServerPlayer> players, ItemEntity itemEntity, FanProcessingType processingType) {
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
