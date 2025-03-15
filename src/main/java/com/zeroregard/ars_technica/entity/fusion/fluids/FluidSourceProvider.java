package com.zeroregard.ars_technica.entity.fusion.fluids;


import com.zeroregard.ars_technica.helpers.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class FluidSourceProvider {
    private final FluidStack fluidStack;
    private final BlockPos sourcePos;
    private final @Nullable FluidState fluidState;
    private final @Nullable IFluidHandler tankSource;

    public FluidSourceProvider(FluidStack fluidStack, @Nullable BlockPos sourcePos, IFluidHandler tankSource) {
        this.fluidStack = fluidStack;
        this.sourcePos = sourcePos;
        this.tankSource = tankSource;
        this.fluidState = null;
    }

    public FluidSourceProvider(FluidStack fluidStack, @Nullable BlockPos sourcePos, FluidState fluidState) {
        this.fluidStack = fluidStack;
        this.sourcePos = sourcePos;
        this.fluidState = fluidState;
        this.tankSource = null;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }

    public int getMbAmount() {
        if (tankSource != null) {
            // TODO: what should the index be here, do we loop over all tanks?
            // TODO: should it also be multiplied?
            return tankSource.getFluidInTank(0).getAmount();
        }
        return fluidState.getAmount() * FluidHelper.FLUID_TO_MB_MULTIPLIER;
    }

    public void drainFluid(int mbToDrain, Level world) {
        if (tankSource != null) {
            tankSource.drain(mbToDrain, IFluidHandler.FluidAction.EXECUTE);
        } else if (fluidState != null) {
            int newAmount = fluidStack.getAmount() - (mbToDrain/ FluidHelper.FLUID_TO_MB_MULTIPLIER);
            fluidStack.setAmount(newAmount);
            if(newAmount == 0) {
                world.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}
