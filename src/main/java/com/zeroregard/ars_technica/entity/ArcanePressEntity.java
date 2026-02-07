package com.zeroregard.ars_technica.entity;

import com.simibubi.create.AllSoundEvents;
import com.zeroregard.ars_technica.entity.fusion.fluids.ArcaneFusionFluids;
import com.zeroregard.ars_technica.entity.fusion.fluids.FluidSourceProvider;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.helpers.RecipeHelpers.CompactingMatch;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
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

public class ArcanePressEntity extends ArcaneProcessEntity implements GeoEntity {

    private static final int TICKS_TO_PRESS_RATIO = 20;
    private static final int TICKS_TO_RESET_RATIO = 40;

    private List<ItemEntity> compactEntities;
    private List<FluidSourceProvider> compactFluids;
    private Vec3 compactMatchPosVec;

    private List<ItemEntity> packEntities;
    private BlockPos packPos;
    private int packGridSize;

    public ArcanePressEntity(Vec3 position, Level world, int maxAmountToPress, float speed, Color color, List<ItemEntity> pressableEntities) {
        super(EntityRegistry.ARCANE_PRESS_ENTITY.get(), position, world, maxAmountToPress, speed, color, pressableEntities);
    }

    public ArcanePressEntity(EntityType<ArcanePressEntity> entityType, Level world) {
        super(entityType, world);
    }

    public void setCompactMode(List<ItemEntity> entities, List<FluidSourceProvider> fluids, Vec3 matchPosVec) {
        this.compactEntities = entities;
        this.compactFluids = fluids;
        this.compactMatchPosVec = matchPosVec;
        this.packEntities = null;
        this.packPos = null;
    }

    public void setPackMode(List<ItemEntity> entities, BlockPos pos, int gridSize) {
        this.packEntities = entities;
        this.packPos = pos;
        this.packGridSize = gridSize;
        this.compactEntities = null;
        this.compactFluids = null;
        this.compactMatchPosVec = null;
    }

    public void setPressMode() {
        this.compactEntities = null;
        this.compactFluids = null;
        this.compactMatchPosVec = null;
        this.packEntities = null;
        this.packPos = null;
    }

    @Override
    public void tick() {
        if (compactEntities != null) {
            tickCompactMode();
            return;
        }
        if (packEntities != null) {
            tickPackMode();
            return;
        }
        super.tick();
    }

    private void tickCompactMode() {
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
            ArsTechnica.LOGGER.info("[Compact tick] tickCount={} ticksToPress={} entities={} fluids={}", tickCount, ticksToPress, compactEntities != null ? compactEntities.size() : -1, compactFluids != null ? compactFluids.size() : -1);
            Optional<CompactingMatch> match = RecipeHelpers.findCompactingMatch(compactEntities, compactFluids, compactMatchPosVec, world);
            if (match.isEmpty()) {
                ArsTechnica.LOGGER.info("[Compact tick] no match, discarding");
                discard();
                return;
            }
            ArsTechnica.LOGGER.info("[Compact tick] executing match recipe={}", match.get().recipe().id());
            executeCompactingMatch(match.get(), world, position());
            amountProcessed++;
        }
        if (tickCount >= ticksToReset) {
            tickCount = 0;
        }
        tickCount++;
    }

    private void tickPackMode() {
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
        ItemStack outputStack = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
        ArsTechnica.LOGGER.info("[executeCompactingMatch] recipe={} resultsSize={} outputEmpty={} output={}", match.recipe().id(), results.size(), outputStack.isEmpty(), outputStack.isEmpty() ? "n/a" : outputStack.getItem());
        if (!outputStack.isEmpty()) {
            ItemEntity outputEntity = new ItemEntity(world, spawnPos.x, spawnPos.y, spawnPos.z, outputStack.copy());
            outputEntity.setDeltaMovement(Vec3.ZERO);
            outputEntity.setPickUpDelay(10);
            world.addFreshEntity(outputEntity);
        }
        BlockPos blockPos = BlockPos.containing(spawnPos);
        AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(world, blockPos, .5f, .75f);
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