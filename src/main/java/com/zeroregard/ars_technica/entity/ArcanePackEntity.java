package com.zeroregard.ars_technica.entity;

import com.simibubi.create.AllSoundEvents;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.util.Color;

import java.util.List;

public class ArcanePackEntity extends AbstractDensificationEntity {

    private static final int TICKS_TO_PRESS_RATIO = 20;
    private static final int TICKS_TO_RESET_RATIO = 40;

    private List<ItemEntity> packEntities;
    private BlockPos packPos;
    private int packGridSize;

    public ArcanePackEntity(Vec3 position, Level world, int maxToProcess, float speed, Color color,
                            List<ItemEntity> entities, BlockPos pos, int gridSize) {
        super(EntityRegistry.ARCANE_PACK_ENTITY.get(), position, world, maxToProcess, speed, color, entities != null ? entities : List.of());
        this.packEntities = entities;
        this.packPos = pos;
        this.packGridSize = gridSize;
    }

    public ArcanePackEntity(EntityType<? extends ArcanePackEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        if (world.isClientSide()) {
            tickCount++;
            return;
        }
        int ticksToPress = Math.max(1, Math.round(TICKS_TO_PRESS_RATIO / speed));
        int ticksToReset = Math.max(ticksToPress * 2, Math.round(TICKS_TO_RESET_RATIO / speed));
        if (amountProcessed >= maxToProcess) {
            discard();
            return;
        }
        if (tickCount == ticksToPress) {
            boolean didPack = RecipeHelpers.tryDoOnePack(packEntities, packPos, packGridSize, world);
            if (!didPack) {
                discard();
                return;
            }
            amountProcessed++;
            AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(world, blockPosition(), .5f, .75f + (speed / 16));
        }
        if (tickCount >= ticksToReset) {
            tickCount = 0;
        }
        tickCount++;
    }

    @Override
    protected boolean canProcessStack(ItemStack stack) {
        return false;
    }

    @Override
    protected void process(ItemEntity item) {
    }
}
