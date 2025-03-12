package com.zeroregard.ars_technica.entity;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
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
import java.util.Optional;

public class ArcanePolishEntity extends ArcaneProcessEntity implements GeoEntity {
    protected double distanceToItem = 0.5;
    public ArcanePolishEntity(Vec3 position, Level world, int maxAmountToPolish, float speed, Color color, List<ItemEntity> polishableEntities) {
        super(EntityRegistry.ARCANE_POLISH_ENTITY.get(), position, world, maxAmountToPolish, speed, color, polishableEntities);
    }

    public ArcanePolishEntity(EntityType<ArcanePolishEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if(tickCount == 1) {
            AllSoundEvents.SANDING_SHORT.playOnServer(world, blockPosition(), .5f,
                    .75f + (speed / 8));
        }
    }


    @Override
    protected void process(ItemEntity item) {
        polish(item);
    }

    private void polish(ItemEntity item) {
        ItemStack currentStack = item.getItem();
        if (currentStack.getCount() > 0) {
            var polishingRecipes = SandPaperPolishingRecipe.getMatchingRecipes(world, currentStack);


            if (polishingRecipes.isEmpty()) {
                processableEntities.remove(currentItem);
                currentItem = null;
                return;
            }

            var currentPos = currentItem.getPosition(1.0f);
            setPos(currentPos.add(Math.random() / 8f, distanceToItem, Math.random() / 8f));

            RegistryAccess registryAccess = world.registryAccess();
            var firstRecipe = polishingRecipes.getFirst();
            var recipe = firstRecipe.value();
            ItemStack polishedStack = recipe.getResultItem(registryAccess);
            polishedStack.setCount(1);

            growOutput(item, polishedStack);

            currentStack.shrink(1);

            amountProcessed++;

            AllSoundEvents.CONTROLLER_PUT.playOnServer(world, blockPosition(), .75f,
                    1.5f + (speed / 16));
        }

        if (currentStack.getCount() <= 0) {
            currentItem = null;
        }
    }

    @Override
    protected void moveToItem() {
        setPos(currentItem.position().add(0, distanceToItem, 0));
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "polishController", 0, this::polishAnimationPredicate));
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    private PlayState polishAnimationPredicate(AnimationState<?> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("polish"));
        event.getController().setAnimationSpeed(speed);
        return PlayState.CONTINUE;
    }

}