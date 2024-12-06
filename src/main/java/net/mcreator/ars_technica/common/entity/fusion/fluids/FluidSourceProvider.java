package net.mcreator.ars_technica.common.entity.fusion.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

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

    public BlockPos getSourcePos() {
        return sourcePos;
    }

    public @Nullable IFluidHandler getTankSource() {
        return tankSource;
    }

    public FluidState getFluidState() {
        return fluidState;
    }
}
