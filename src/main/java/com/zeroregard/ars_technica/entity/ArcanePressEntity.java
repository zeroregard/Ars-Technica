package com.zeroregard.ars_technica.entity;

import com.simibubi.create.AllSoundEvents;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.helpers.RecipeHelpers.CompactingMatch;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class ArcanePressEntity extends ArcaneProcessEntity implements GeoEntity {

    private static final int TICKS_TO_PRESS_RATIO = 20;

    private CompactingMatch compactJob;

    public ArcanePressEntity(Vec3 position, Level world, int maxAmountToPress, float speed, Color color, List<ItemEntity> pressableEntities) {
        super(EntityRegistry.ARCANE_PRESS_ENTITY.get(), position, world, maxAmountToPress, speed, color, pressableEntities);
    }

    public ArcanePressEntity(EntityType<ArcanePressEntity> entityType, Level world) {
        super(entityType, world);
    }

    public void setCompactJob(CompactingMatch match) {
        this.compactJob = match;
    }

    @Override
    public void tick() {
        if (compactJob != null) {
            if (world.isClientSide()) {
                tickCount++;
                return;
            }
            int ticksToPress = Math.max(1, Math.round(TICKS_TO_PRESS_RATIO / speed));
            if (tickCount >= ticksToPress) {
                executeCompact();
                discard();
                return;
            }
            tickCount++;
            return;
        }
        super.tick();
    }

    private void executeCompact() {
        if (compactJob == null || world.isClientSide()) return;
        for (CompactingMatch.ConsumptionEntry entry : compactJob.consumption()) {
            ItemEntity e = entry.entity();
            if (e.isRemoved()) continue;
            ItemStack stack = e.getItem();
            stack.shrink(entry.count());
            if (stack.isEmpty()) {
                e.discard();
            }
        }
        var recipe = compactJob.recipe().value();
        List<ItemStack> results = recipe.rollResults(world.random);
        ItemStack outputStack = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
        if (!outputStack.isEmpty()) {
            Vec3 at = position();
            ItemEntity outputEntity = new ItemEntity(world, at.x, at.y, at.z, outputStack.copy());
            outputEntity.setDeltaMovement(Vec3.ZERO);
            outputEntity.setPickUpDelay(10);
            world.addFreshEntity(outputEntity);
        }
        AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(world, blockPosition(), .5f, .75f + (speed / 16));
        compactJob = null;
    }

    @Override
    protected void process(ItemEntity item) {
        press(item);
    }

    private void press(ItemEntity item) {
        ItemStack currentStack = item.getItem();
        if (currentStack.getCount() > 0) {
            var pressingRecipe = RecipeHelpers.getPressingRecipeForItemStack(currentStack, world);

            if (pressingRecipe.isEmpty()) {
                if (currentItem != null) {
                    processableEntities.remove(currentItem);
                    currentItem = null;
                }
                return;
            }

            if (currentItem != null) {
                var currentPos = currentItem.getPosition(1.0f);
                setPos(currentPos.add(0, 1f, 0));
            }

            RegistryAccess registryAccess = world.registryAccess();
            var recipe = pressingRecipe.get().value();
            ItemStack pressedStack = recipe.getResultItem(registryAccess);
            pressedStack.setCount(1);

            growOutput(item, pressedStack);

            currentStack.shrink(1);

            amountProcessed++;

            AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(world, blockPosition(), .5f,
                    .75f + (speed / 16));
        }

        if (currentStack.getCount() <= 0 && currentItem != null) {
            currentItem = null;
        }
    }

    @Override
    protected boolean canProcessStack(ItemStack stack) {
        var pressingRecipe = RecipeHelpers.getPressingRecipeForItemStack(stack, world);
        return pressingRecipe.isPresent();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "pressController", 0, this::pressAnimationPredicate));
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    private PlayState pressAnimationPredicate(AnimationState<?> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("press"));
        event.getController().setAnimationSpeed(speed);
        return PlayState.CONTINUE;
    }

}