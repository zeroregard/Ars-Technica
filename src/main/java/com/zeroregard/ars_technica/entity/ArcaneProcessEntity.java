package com.zeroregard.ars_technica.entity;

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
import software.bernie.geckolib.util.Color;

import java.util.Comparator;
import java.util.List;

public abstract class ArcaneProcessEntity extends Entity implements Colorable {

    private static int TICKS_TO_PROCESS_RATIO = 20;
    private static int TICKS_TO_RESET_RATIO = TICKS_TO_PROCESS_RATIO * 2;
    protected double distanceToItem = 1.0;

    protected int maxToProcess = 1;
    protected float speed = 2.0f;
    protected int amountProcessed = 0;
    protected final List<ItemEntity> processableEntities;
    protected ItemEntity currentItem;
    protected ItemEntity currentOutput;
    protected int tickCount;
    protected Color color;
    protected Level world;

    protected static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.INT);


    private int getTicksToPress() {
        return Math.round(TICKS_TO_PROCESS_RATIO / speed);
    }

    private int getTicksToReset() {
        return Math.round(TICKS_TO_RESET_RATIO / speed);
    }

    protected void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    protected void setColor(Color color) {
        this.entityData.set(COLOR, color.getColor());
    }

    public ArcaneProcessEntity(EntityType<?> entityType, Vec3 position, Level world, int maxToProcess, float speed, Color color, List<ItemEntity> processableEntities) {
        super(entityType, world);
        this.setPos(position.x, position.y, position.z);
        this.maxToProcess = maxToProcess;
        this.speed = speed;
        setSpeed(speed);
        setColor(color);
        this.processableEntities = processableEntities;
        this.world = world;
    }

    public ArcaneProcessEntity(EntityType<? extends ArcaneProcessEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
        this.processableEntities = null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder dataBuilder) {
        dataBuilder.define(SPEED, 2.0f);
        dataBuilder.define(COLOR, 0);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (SPEED.equals(key)) {
            this.speed = this.entityData.get(SPEED);
        }

        if (COLOR.equals(key)) {
            this.color = new Color(this.entityData.get(COLOR));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (currentItem == null || currentItem.isRemoved()) {
            findNextItem();
        }

        if ((amountProcessed == maxToProcess || currentItem == null) && !world.isClientSide) {
            this.discard();
        }

        handleProcessLogic();

        if (tickCount >= getTicksToReset()) {
            if(currentItem != null && !currentItem.isRemoved()) {
                moveToItem();
            }
            tickCount = 0;
        }
        tickCount++;
    }

    protected void growOutput(ItemEntity input, ItemStack outputStack) {
        if (currentOutput != null && currentOutput.getItem().getItem() == outputStack.getItem() && currentOutput.getItem().getCount() < currentOutput.getItem().getMaxStackSize()) {
            ItemStack currentOutputItem = currentOutput.getItem();
            ItemStack newStack = currentOutputItem.copyWithCount(currentOutputItem.getCount() + 1);
            currentOutput.discard();

            currentOutput = new ItemEntity(world, input.getX(), input.getY(), input.getZ(), newStack);
            world.addFreshEntity(currentOutput);
        } else {
            currentOutput = new ItemEntity(world, input.getX(), input.getY(), input.getZ(), outputStack);
            world.addFreshEntity(currentOutput);
        }
    }

    protected void moveToItem() {
        setPos(currentItem.position().add(0, distanceToItem, 0));
    }


    private void findNextItem() {
        if (processableEntities == null) {
            return;
        }
        currentItem = processableEntities.stream()
                .filter(x -> x != null && !x.isRemoved() && x.getItem().getCount() > 0)
                .min(Comparator.comparingDouble(e -> e.position().distanceTo(getPosition(1.0f))))
                .orElse(null);
    }

    private void handleProcessLogic() {
        if (currentItem != null) {
            if (tickCount == getTicksToPress() && !world.isClientSide()) {
                if (currentItem.isRemoved()) {
                    currentItem = null;
                } else {
                    process(currentItem);
                }
            }
        }
    }

    protected abstract void process(ItemEntity item);

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Speed")) {
            this.speed = compound.getFloat("Speed");
            this.entityData.set(SPEED, this.speed);
        }
        if(compound.contains("Color")) {
            this.color = new Color(compound.getInt("Color"));
            this.entityData.set(COLOR, this.color.getColor());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("Speed", this.speed);
        compound.putInt("Color", this.color.getColor());
    }


    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public double getAlpha() {
        return 1;
    }
}