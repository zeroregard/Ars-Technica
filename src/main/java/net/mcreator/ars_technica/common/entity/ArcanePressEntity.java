package net.mcreator.ars_technica.common.entity;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;

public class ArcanePressEntity extends Entity implements GeoEntity {

    private static int TICKS_TO_PRESS_RATIO = 20;
    private static int TICKS_TO_RESET_RATIO = TICKS_TO_PRESS_RATIO * 2;

    private int maxAmountToPress = 1;
    private float speed = 2.0f;
    private int amountPressed = 0;
    private final List<ItemEntity> pressableEntities;
    private ItemEntity currentItem;
    private int tickCount;
    private Level world;

    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(ArcanePressEntity.class, EntityDataSerializers.FLOAT);

    private int getTicksToPress() {
        return Math.round(TICKS_TO_PRESS_RATIO / speed);
    }

    private int getTicksToReset() {
        return Math.round(TICKS_TO_RESET_RATIO / speed);
    }

    private void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    public ArcanePressEntity(Vec3 position, Level world, int maxAmountToPress, float speed, List<ItemEntity> pressableEntities) {
        super(EntityRegistry.ARCANE_PRESS_ENTITY.get(), world);
        this.setPos(position.x, position.y, position.z);
        this.maxAmountToPress = maxAmountToPress;
        this.speed = speed;
        setSpeed(speed);
        this.pressableEntities = pressableEntities;
        this.world = world;
    }

    public ArcanePressEntity(EntityType<ArcanePressEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
        this.pressableEntities = null;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPEED, 2.0f);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (SPEED.equals(key)) {
            this.speed = this.entityData.get(SPEED);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (currentItem == null || currentItem.isRemoved()) {
            findNextItem();
        }

        if ((amountPressed == maxAmountToPress || currentItem == null) && !world.isClientSide) {
            this.discard();
        }

        handlePressLogic();

        if (tickCount >= getTicksToReset()) {
            if(currentItem != null && !currentItem.isRemoved()) {
                setPos(currentItem.position().add(0, 1.0, 0));
            }
            tickCount = 0;
        }
        tickCount++;
    }


    private void findNextItem() {
        if (pressableEntities == null) {
            return;
        }
        for (ItemEntity item : pressableEntities) {
            if (item != null && !item.isRemoved() && item.getItem().getCount() > 0) {
                currentItem = item;
                break;
            }
        }
    }

    private void handlePressLogic() {
        if (currentItem != null) {
            if (tickCount == getTicksToPress() && !world.isClientSide()) {
                if (currentItem.isRemoved()) {
                    currentItem = null;
                } else {
                    press(currentItem);
                }
            }
        }
    }

    private void press(ItemEntity item) {
        ItemStack currentStack = item.getItem();
        if (currentStack.getCount() > 0) {
            Optional<PressingRecipe> pressingRecipe = RecipeHelpers.getPressingRecipeForItemStack(currentStack, world);

            if (pressingRecipe.isEmpty()) {
                pressableEntities.remove(currentItem);
                currentItem = null;
                return;
            }

            RegistryAccess registryAccess = world.registryAccess();
            PressingRecipe recipe = pressingRecipe.get();
            ItemStack pressedStack = recipe.getResultItem(registryAccess);
            pressedStack.setCount(1);
            ItemEntity pressedEntity = new ItemEntity(world, item.getX(), item.getY(), item.getZ(), pressedStack);
            world.addFreshEntity(pressedEntity);


            currentStack.shrink(1);

            amountPressed++;
            AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(world, blockPosition(), .5f,
                    .75f + (speed / 8));
        }

        if (currentStack.getCount() <= 0) {
            currentItem = null;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Speed")) {
            this.speed = compound.getFloat("Speed");
            this.entityData.set(SPEED, this.speed);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("Speed", this.speed);
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