package com.zeroregard.ars_technica.entity.fusion.fluids;

import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class ArcaneFusionFluids {
    private final Level level;
    private final ArcaneFusionEntity parent;

    public ArcaneFusionFluids(ArcaneFusionEntity parent, Level level) {
        this.level = level;
        this.parent = parent;
    }

    /**
     * Picks up fluids from the world and returns a list of fluid data.
     * Fluids in this case can also be storage tanks etc.
     *
     * @return List of FluidSourceProvider, representing either FluidState or IFluidHandler
     */
    public List<FluidSourceProvider> pickupFluids() {
        List<FluidSourceProvider> results = new ArrayList<>();
        BlockPos center = new BlockPos(parent.getBlockX(), parent.getBlockY(), parent.getBlockZ());
        int range = 5; // TODO: get range from AOE ( ? )

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
            BlockState blockState = level.getBlockState(pos);
            FluidState fluidState = blockState.getFluidState();

            if (!fluidState.isEmpty() && fluidState.isSource()) {
                // Fluid dropped in the world
                FluidStack fluidStack = new FluidStack(fluidState.getType(), fluidState.getAmount());
                results.add(new FluidSourceProvider(fluidStack, pos, fluidState));
            }  else {
                // No direct fluid state - check if there's a fluid tank at this position and try draining fluid
                BlockEntity blockEntity = level.getBlockEntity(pos);
                var fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(level, pos, blockState, blockEntity, null);
                if (blockEntity != null && fluidHandler != null) {
                    // Simulate draining from the tank to get the amount of fluid available
                    FluidStack fluidStack = fluidHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE); // Simulate drain for a bucket
                    if (!fluidStack.isEmpty()) {
                        results.add(new FluidSourceProvider(fluidStack, null, fluidHandler));
                    }
                }
            }
        }

        return results;
    }
}
