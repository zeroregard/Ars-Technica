package net.mcreator.ars_technica.common.entity;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;

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
            Optional<PressingRecipe> pressingRecipe = RecipeHelpers.getPressingRecipeForItemStack(currentStack, world);

            if (pressingRecipe.isEmpty()) {
                processableEntities.remove(currentItem);
                currentItem = null;
                return;
            }

            var currentPos = currentItem.getPosition(1.0f);
            setPos(currentPos.add(Math.random() / 4f, 1f, Math.random() / 4f));

            RegistryAccess registryAccess = world.registryAccess();
            PressingRecipe recipe = pressingRecipe.get();
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