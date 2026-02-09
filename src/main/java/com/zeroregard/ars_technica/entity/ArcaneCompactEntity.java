package com.zeroregard.ars_technica.entity;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.zeroregard.ars_technica.entity.fusion.fluids.ArcaneFusionFluids;
import com.zeroregard.ars_technica.entity.fusion.fluids.FluidSourceProvider;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.helpers.RecipeHelpers.CompactingMatch;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.util.Color;

import java.util.List;
import java.util.Optional;

public class ArcaneCompactEntity extends AbstractDensificationEntity {

    private static final int TICKS_TO_PRESS_RATIO = 20;
    private static final int TICKS_TO_RESET_RATIO = 40;

    private List<ItemEntity> compactEntities;
    private List<FluidSourceProvider> compactFluids;
    private Vec3 compactMatchPosVec;
    private HeatCondition suppliedHeat;

    public ArcaneCompactEntity(Vec3 position, Level world, int maxToProcess, float speed, Color color,
                               List<ItemEntity> entities, List<FluidSourceProvider> fluids, Vec3 matchPosVec, HeatCondition suppliedHeat) {
        super(EntityRegistry.ARCANE_COMPACT_ENTITY.get(), position, world, maxToProcess, speed, color, entities != null ? entities : List.of());
        this.compactEntities = entities;
        this.compactFluids = fluids;
        this.compactMatchPosVec = matchPosVec != null ? matchPosVec : position;
        this.suppliedHeat = suppliedHeat != null ? suppliedHeat : HeatCondition.NONE;
    }

    public ArcaneCompactEntity(EntityType<? extends ArcaneCompactEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        if (world.isClientSide()) {
            tickCount++;
            return;
        }
        if (compactFluids == null) {
            compactFluids = ArcaneFusionFluids.pickupFluidsAround(world, blockPosition(), 8);
        }
        if (compactEntities == null) {
            compactEntities = List.of();
        }
        if (compactMatchPosVec == null) {
            compactMatchPosVec = position();
        }
        int ticksToPress = Math.max(1, Math.round(TICKS_TO_PRESS_RATIO / speed));
        int ticksToReset = Math.max(ticksToPress * 2, Math.round(TICKS_TO_RESET_RATIO / speed));
        if (amountProcessed >= maxToProcess) {
            discard();
            return;
        }
        if (tickCount == ticksToPress) {
            HeatCondition heat = suppliedHeat != null ? suppliedHeat : HeatCondition.NONE;
            Optional<CompactingMatch> match = RecipeHelpers.findCompactingMatch(compactEntities, compactFluids, compactMatchPosVec, world, heat);
            if (match.isEmpty()) {
                discard();
                return;
            }
            executeCompactingMatch(match.get(), world, position());
            amountProcessed++;
        }
        if (tickCount >= ticksToReset) {
            tickCount = 0;
        }
        tickCount++;
    }

    public static void executeCompactingMatch(CompactingMatch match, Level world, Vec3 spawnPos) {
        if (world.isClientSide()) return;
        for (CompactingMatch.ConsumptionEntry entry : match.consumption()) {
            ItemEntity e = entry.entity();
            if (e.isRemoved()) continue;
            ItemStack stack = e.getItem();
            stack.shrink(entry.count());
            if (stack.isEmpty()) {
                e.discard();
            }
        }
        for (CompactingMatch.FluidConsumptionEntry entry : match.fluidConsumption()) {
            entry.provider().drainFluid(entry.amountMb(), world);
        }
        var recipe = match.recipe().value();
        List<ItemStack> results = recipe.rollResults(world.random);
        for (ItemStack outputStack : results) {
            if (outputStack.isEmpty()) continue;
            ItemEntity outputEntity = new ItemEntity(world, spawnPos.x, spawnPos.y, spawnPos.z, outputStack.copy());
            outputEntity.setDeltaMovement(Vec3.ZERO);
            outputEntity.setPickUpDelay(10);
            world.addFreshEntity(outputEntity);
        }
        // Note: fluid results (getFluidResults()) are not emitted — we have no basin to fill
        BlockPos blockPos = BlockPos.containing(spawnPos);
        AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(world, blockPos, .5f, .75f);
    }

    @Override
    protected boolean canProcessStack(ItemStack stack) {
        return false;
    }

    @Override
    protected void process(ItemEntity item) {
    }
}
