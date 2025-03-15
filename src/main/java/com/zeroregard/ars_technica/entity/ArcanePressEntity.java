package com.zeroregard.ars_technica.entity;

import com.simibubi.create.AllSoundEvents;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
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

public class ArcanePressEntity extends ArcaneProcessEntity implements GeoEntity, Colorable {


    public ArcanePressEntity(Vec3 position, Level world, int maxAmountToPress, float speed, Color color, List<ItemEntity> pressableEntities) {
        super(EntityRegistry.ARCANE_PRESS_ENTITY.get(), position, world, maxAmountToPress, speed, color, pressableEntities);
    }

    public ArcanePressEntity(EntityType<ArcanePressEntity> entityType, Level world) {
        super(entityType, world);
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
                processableEntities.remove(currentItem);
                currentItem = null;
                return;
            }

            var currentPos = currentItem.getPosition(1.0f);
            setPos(currentPos.add(0, 1f, 0));

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

        if (currentStack.getCount() <= 0) {
            currentItem = null;
        }
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