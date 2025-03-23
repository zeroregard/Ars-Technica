package com.zeroregard.ars_technica.entity;

import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.zeroregard.ars_technica.helpers.ConsumptionHelper;
import com.zeroregard.ars_technica.helpers.ProjectileHelper;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemProjectileEntity extends Entity {
    private ItemStack itemStack;
    private Vec3 velocity;
    private final Level world;
    private static final EntityDataAccessor<ItemStack> ITEM_STACK = SynchedEntityData.defineId(ItemProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final int MAXIMUM_LIFE_TIME_TICKS = 10 * 20;
    private int ticks = 0;

    public @Nullable ItemStack getStack() {
        return itemStack;
    }

    public ItemProjectileEntity(EntityType<ItemProjectileEntity> type, Level world) {
        super(type, world);
        this.itemStack = ItemStack.EMPTY;
        this.velocity = Vec3.ZERO;
        this.world = world;
    }

    public ItemProjectileEntity(Level world, Vec3 position, Vec3 direction, ItemStack itemStack) {
        this(EntityRegistry.ITEM_PROJECTILE_ENTITY.get(), world);
        this.setPos(position);
        this.velocity = direction.scale(0.2);
        this.itemStack = itemStack.copy();
        this.entityData.set(ITEM_STACK, itemStack);
    }

    @Override
    public void tick() {
        super.tick();

        ticks++;
        if(ticks >= MAXIMUM_LIFE_TIME_TICKS) {
            this.discard();
        }

        this.setDeltaMovement(velocity);
        this.move(MoverType.SELF, velocity);

        if (!this.world.isClientSide) {
            var rayTrace = ProjectileHelper.getHitResult(this, this::shouldEntityCollide, this::shouldBlockEntityCollide);
            if (rayTrace != null) {
                onImpact(rayTrace);
                this.discard();
            }
        }

        if (!this.world.isInWorldBounds(this.blockPosition())) {
            this.discard();
        }
    }

    private boolean shouldEntityCollide(Entity entity) {
        return entity instanceof LivingEntity;
    }

    private <T extends BlockEntity> boolean shouldBlockEntityCollide(T entity) {
        if (entity instanceof PortalTile) {
            return false;
        }
        var blockPos = entity.getBlockPos();
        var blockState = entity.getBlockState();
        var itemHandler = Capabilities.ItemHandler.BLOCK.getCapability(world, blockPos, blockState, entity, null);
        var isItemHandler = itemHandler != null;
        var fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(world, blockPos, blockState, entity, null);
        var isFluidHandler = fluidHandler != null;
        return !(isItemHandler || isFluidHandler);
    }

    private void onImpact(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHit) {
            handleEntityImpact(entityHit.getEntity());
        } else if (hitResult instanceof BlockHitResult blockHit) {
            handleBlockImpact(blockHit.getBlockPos());
        }
    }

    private void handleEntityImpact(Entity entity) {
        if (entity instanceof LivingEntity target) {
            if (ConsumptionHelper.tryUseEdibleItem(target, itemStack, world)) {
                return;
            }

            if(ConsumptionHelper.tryUseConsumableItem(target, itemStack, world, true)) {
                return;
            }
        }
    }

    private void handleBlockImpact(BlockPos pos) {
        discard();
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder dataBuilder) {
        dataBuilder.define(ITEM_STACK, ItemStack.EMPTY);
    }


    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(ITEM_STACK.equals(key)) {
            itemStack = this.entityData.get(ITEM_STACK);
        }
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Use the level's registry access as the lookup provider.
        itemStack = ItemStack.parseOptional(world.registryAccess(), tag.getCompound("Item"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("Item", itemStack.save(world.registryAccess(), new CompoundTag()));
    }
    @Override
    public boolean isPickable() {
        return false; // Prevents the entity from being picked up
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}