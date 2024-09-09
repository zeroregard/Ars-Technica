package net.mcreator.ars_technica.common.entity;

import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WhirlEntity extends Entity {

    private final double radius;
    private final double radius_pull_margin = 0.2;
    private int duration;
    private final Level world;
    private double speed = 0.2;
    private final Set<ItemEntity> affectedItems = new HashSet<>();

    public WhirlEntity(EntityType<? extends WhirlEntity> entityType, Level world) {
        super(entityType, world);
        this.radius = 1.0;
        this.duration = 100;
        this.world = world;
    }

    public WhirlEntity(Level world, Vec3 position, double radius, int duration) {
        super(EntityRegistry.WHIRL_ENTITY.get(), world);
        this.setPos(position.x, position.y, position.z);
        this.radius = radius;
        this.duration = duration;
        this.world = world;
    }

    @Override
    public void tick() {
        super.tick();

        if (duration-- <= 0) {
            stopAffectedItems();
            this.discard();
            return;
        }

        Vec3 centerPos = new Vec3(getX(), getY(), getZ());
        AABB whirlwindBounds = new AABB(centerPos.subtract(radius, radius, radius),
                centerPos.add(radius, radius, radius));

        List<ItemEntity> nearbyItems = world.getEntitiesOfClass(ItemEntity.class, whirlwindBounds);

        if (!world.isClientSide()) {
            for (ItemEntity item : nearbyItems) {
                Vec3 itemPos = item.position();
                Vec3 toCenter = centerPos.subtract(itemPos).normalize();

                double distance = itemPos.distanceTo(centerPos);
                double distanceFromEdge = distance - radius;

                if (distanceFromEdge > radius_pull_margin) {
                    Vec3 adjustVector = toCenter.scale(distanceFromEdge - radius_pull_margin);
                    item.setPos(itemPos.add(adjustVector));
                }

                Vec3 tangent = new Vec3(-toCenter.z, 0, toCenter.x).normalize();
                Vec3 spinVelocity = tangent.scale(speed);

                Vec3 currentVelocity = item.getDeltaMovement();
                Vec3 newVelocity = new Vec3(spinVelocity.x, currentVelocity.y, spinVelocity.z);

                item.setDeltaMovement(newVelocity);
                item.hurtMarked = true;

                affectedItems.add(item);
            }
        }
    }


    private void stopAffectedItems() {
        if (!world.isClientSide()) {
            for (ItemEntity item : affectedItems) {
                Vec3 currentVelocity = item.getDeltaMovement();

                Vec3 friction = new Vec3(0.2, 1.0, 0.2);
                Vec3 newVelocity = currentVelocity.multiply(friction);

                item.setDeltaMovement(newVelocity);
                item.hurtMarked = true;
            }
            affectedItems.clear();
        }
    }

    @Override
    protected void defineSynchedData() {
        // TBD
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.duration = compound.getInt("Duration");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Duration", this.duration);
    }
}