package net.mcreator.ars_technica.common.entity;

import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import net.mcreator.ars_technica.common.helpers.ConsumptionHelper;
import net.mcreator.ars_technica.common.helpers.ProjectileHelper;
import net.mcreator.ars_technica.setup.EntityRegistry;
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
import net.minecraft.world.phys.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public class ItemProjectileEntity extends Entity {
    private ItemStack itemStack;
    private Vec3 velocity;
    private final Level world;
    private static final EntityDataAccessor<ItemStack> ITEM_STACK = SynchedEntityData.defineId(ItemProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final int MAXIMUM_LIFE_TIME_TICKS = 10 * 20;
    private int ticks = 0;

    public ItemStack getStack() {
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

    private <T extends CapabilityProvider<?>> boolean shouldBlockEntityCollide(T entity) {
        if (entity instanceof PortalTile) {
            return false;
        }
        var isItemHandler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
        var isFluidHandler = entity.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent();
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
    protected void defineSynchedData() {
        this.entityData.define(ITEM_STACK, ItemStack.EMPTY);
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
        itemStack = ItemStack.of(tag.getCompound("Item"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("Item", itemStack.save(new CompoundTag()));
    }

    @Override
    public boolean isPickable() {
        return false; // Prevents the entity from being picked up
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}