package net.mcreator.ars_technica.common.helpers;

import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Comparator.*;

public class FluidHelper {
    public static int BLOCK_MAX_FLUID_LEVEL = 8;
    public static int FLUID_MB_MAX_IN_BLOCK = 1000;
    public static int FLUID_TO_MB_MULTIPLIER = FLUID_MB_MAX_IN_BLOCK / BLOCK_MAX_FLUID_LEVEL;

    public static void dumpFluid(FluidStack stack, Level world, Vec3 position, int expansion) {

        AtomicInteger remainingAmount = new AtomicInteger(stack.getAmount()); // assumes amount in mB
        AABB searchArea = new AABB(
                position.x - expansion, position.y - expansion, position.z - expansion,
                position.x + expansion, position.y + expansion, position.z + expansion
        );

        getNearbyFluidTanks(world, searchArea, position).forEach(tank -> {
            int filled = tank.fill(new FluidStack(stack.getFluid(), Math.min(tank.getTankCapacity(0) - tank.getFluidInTank(0).getAmount(), remainingAmount.get())), IFluidHandler.FluidAction.EXECUTE);
            remainingAmount.addAndGet(-filled);
        });

        boolean canPlaceFluids = ConfigHandler.Common.FLUID_CAN_BE_PLACED.get();
        boolean canPlaceSources = ConfigHandler.Common.FLUID_SOURCES_CAN_BE_PLACED.get();
        int maxFluidsToPlace = ConfigHandler.Common.FLUID_MAX_PLACEMENTS_PER_FUSE.get();
        if(!canPlaceFluids) {
            return;
        }

        // If there is still liquid left, try to place on air blocks nearby
        int fluidsPlaced = 0;
        if (remainingAmount.get() > 0) {
            List<BlockPos> airBlocks = getNearbyAirBlocks(world, searchArea, position);
            for (int i = 0; i < airBlocks.size(); i++) {
                BlockPos airPos = airBlocks.get(i);
                if (remainingAmount.get() <= 0 || fluidsPlaced >= maxFluidsToPlace) {
                    break;
                }
                if (world.getBlockState(airPos).isAir()) {
                    int amountToPlace = Math.min(remainingAmount.get(), FLUID_MB_MAX_IN_BLOCK);
                    int blockStateAmount = amountToPlace / FLUID_TO_MB_MULTIPLIER;
                    var shouldPlaceSource = amountToPlace == FLUID_MB_MAX_IN_BLOCK;
                    var fluid = canPlaceSources && shouldPlaceSource  ? com.simibubi.create.foundation.fluid.FluidHelper.convertToStill(stack.getFluid()) : stack.getFluid();

                    BlockState fluidBlockState = fluid.defaultFluidState().createLegacyBlock();
                    if (!shouldPlaceSource && fluidBlockState.hasProperty(BlockStateProperties.LEVEL)) {
                        fluidBlockState = fluidBlockState.setValue(BlockStateProperties.LEVEL, blockStateAmount);
                    }

                    world.setBlock(airPos, fluidBlockState, 2 | 16);
                    fluidsPlaced++;
                    remainingAmount.addAndGet(-amountToPlace);
                }
            }
        }
        // Still any liquid left? That's too bad
    }


    private static Stream<IFluidHandler> getNearbyFluidTanks(Level world, AABB searchArea, Vec3 position) {
        List<IFluidHandler> handlers = new ArrayList<>();
        BlockPos.betweenClosedStream(searchArea).forEach(pos -> {
            BlockEntity be = world.getBlockEntity(pos);
            if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()) {
                IFluidHandler handler = StarbyFluidBehavior.getHandlerFromCap(pos, world, 0);
                if (handler != null && (handler.getFluidInTank(0).isEmpty() || handler.getFluidInTank(0).getAmount() <= handler.getTankCapacity(0) - FLUID_MB_MAX_IN_BLOCK)) {
                    handlers.add(handler);
                }
            }
        });

        // TODO: Get the position of each tank so we can sort them
        return handlers.stream();
    }

    // The BlockPos functions seem broken so we're rolling our own unfortunately
    private static List<BlockPos> getNearbyAirBlocks(Level world, AABB searchArea, Vec3 position) {
        List<BlockPos> airBlocks = new ArrayList<>();

        int minX = (int) searchArea.minX;
        int maxX = (int) searchArea.maxX;
        int minY = (int) searchArea.minY;
        int maxY = (int) searchArea.maxY;
        int minZ = (int) searchArea.minZ;
        int maxZ = (int) searchArea.maxZ;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(currentPos).getBlock();
                    if (block == Blocks.AIR) {
                        airBlocks.add(currentPos);
                    }
                }
            }
        }

        airBlocks.sort(comparingDouble(pos -> position.distanceToSqr(new Vec3(pos.getX(), pos.getY(), pos.getZ()))));
        return airBlocks;
    }
}
