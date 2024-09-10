package net.mcreator.ars_technica.common.kinetics;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class WhirlCurrent {

    private final WhirlEntity source;
    private AABB bounds;
    private List<ItemEntity> affectedEntities;
    private double radius;
    private final UUID uniqueId;

    public WhirlCurrent(WhirlEntity source) {
        this.source = source;
        this.radius = source.getRadius();
        this.uniqueId = UUID.randomUUID();

        Vec3 centerPos = source.getPosition(1);
        bounds = new AABB(centerPos.subtract(radius, radius, radius),
                centerPos.add(radius, radius, radius));
    }

    public void tick() {
        Level world = source.getLevel();
        if (world == null) return;
        tickAffectedEntities(world);
    }

    protected void tickAffectedEntities(Level world) {
        affectedEntities = world.getEntitiesOfClass(ItemEntity.class, bounds);
        for (Iterator<ItemEntity> iterator = affectedEntities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            if (!entity.isAlive() || !entity.getBoundingBox().intersects(bounds)) { // || !isItemBeingProcessedByThis(entity)) {
                iterator.remove();
                continue;
            }

            Vec3 direction = source.position().subtract(entity.position()).normalize();
            double distance = entity.position().distanceTo(source.position());

            Vec3 motion = entity.getDeltaMovement();
            Vec3 tangentialMotion = new Vec3(-direction.z, 0, direction.x).scale(0.1);
            Vec3 pull = direction.scale(0.05 * (radius - distance));

            entity.setDeltaMovement(motion.add(tangentialMotion).add(pull));
            // setProcessingId(entity, this.uniqueId);
            entity.fallDistance = 0;

            FanProcessingType processingType = source.getProcessor();

            if (processingType == AllFanProcessingTypes.NONE)
                continue;

            if (entity instanceof ItemEntity itemEntity) {
                if (FanProcessing.canProcess(itemEntity, processingType)) {
                    ArsTechnicaMod.LOGGER.info("Processing...");
                    FanProcessing.applyProcessing(itemEntity, processingType);
                }
                if (world != null && world.isClientSide) {
                    ArsTechnicaMod.LOGGER.info("Spawning particles...");
                    processingType.spawnProcessingParticles(world, entity.position());
                }
            }
        }
    }

    public void stopAffectedItems() {
        for (ItemEntity item : affectedEntities) {
            item.setDeltaMovement(Vec3.ZERO);
            item.hurtMarked = true;
            // removeProcessingId(item);
        }
        affectedEntities.clear();
    }

    private static void removeProcessingId(Entity entity) {
        CompoundTag nbt = entity.getPersistentData();
        if (nbt.contains("CreateData")) {
            CompoundTag createData = nbt.getCompound("CreateData");
            if (createData.contains("WhirlwindId")) {
                createData.remove("WhirlwindId");
                if (createData.isEmpty()) {
                    nbt.remove("CreateData");
                }
            }
        }
    }

    private static void setProcessingId(Entity entity, UUID uniqueId) {
        CompoundTag nbt = entity.getPersistentData();
        if (!nbt.contains("CreateData")) {
            nbt.put("CreateData", new CompoundTag());
        }
        CompoundTag createData = nbt.getCompound("CreateData");
        createData.putUUID("WhirlwindId", uniqueId);
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

    private boolean isItemBeingProcessedByThis(Entity entity) {
        UUID processingId = getProcessingId(entity);
        return processingId == null || processingId.equals(this.uniqueId);
    }

}
