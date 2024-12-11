package net.mcreator.ars_technica.common.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import java.util.Optional;
import java.util.function.Predicate;

public class ProjectileHelper {
    public static HitResult getHitResult(Entity projectile, Predicate<Entity> entityFilter, Predicate<BlockEntity> blockEntityFilter) {
        Level world = projectile.level();
        Vec3 start = projectile.getEyePosition(1.0F);
        Vec3 end = start.add(projectile.getDeltaMovement());

        BlockHitResult blockHit = world.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                projectile
        ));

        BlockPos blockPos = blockHit.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.isAir()) {
            blockHit = null;
        } else {
            BlockEntity be = world.getBlockEntity(blockPos);
            if (be != null && !blockEntityFilter.test(be)) {
                blockHit = null;
            }
        }

        if(blockHit != null) {
            return blockHit;
        }

        return getEntityHitResult(world, projectile, start, end, projectile.getBoundingBox().expandTowards(projectile.getDeltaMovement()).inflate(0.5), entityFilter);
    }

    private static EntityHitResult getEntityHitResult(Level world, Entity projectile, Vec3 start, Vec3 end, AABB boundingBox, Predicate<Entity> entityFilter) {
        EntityHitResult closestEntityHit = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : world.getEntities(projectile, boundingBox, entityFilter)) {
            AABB entityBoundingBox = entity.getBoundingBox().inflate(0.5);
            Optional<Vec3> intersection = entityBoundingBox.clip(start, end);

            if (intersection.isPresent()) {
                double distance = start.distanceTo(intersection.get());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntityHit = new EntityHitResult(entity, intersection.get());
                }
            }
        }

        return closestEntityHit;
    }
}
