package com.zeroregard.ars_technica.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * Base for Press, Compact and Pack entities. Shares the same press animation and timing;
 * subclasses implement the actual processing (press recipes, compact recipes, or packing).
 */
public abstract class AbstractDensificationEntity extends ArcaneProcessEntity implements GeoEntity {

    private final AnimatableInstanceCache animCache = GeckoLibUtil.createInstanceCache(this);

    public AbstractDensificationEntity(EntityType<?> entityType, Vec3 position, Level world, int maxToProcess, float speed, software.bernie.geckolib.util.Color color, List<ItemEntity> processableEntities) {
        super(entityType, position, world, maxToProcess, speed, color, processableEntities);
    }

    public AbstractDensificationEntity(EntityType<? extends ArcaneProcessEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "pressController", 0, this::pressAnimationPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animCache;
    }

    protected PlayState pressAnimationPredicate(AnimationState<?> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("press"));
        event.getController().setAnimationSpeed(speed);
        return PlayState.CONTINUE;
    }
}
