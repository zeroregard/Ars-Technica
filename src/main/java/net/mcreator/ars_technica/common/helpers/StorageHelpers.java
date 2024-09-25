package net.mcreator.ars_technica.common.helpers;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.entity.decoration.ItemFrame;

import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StorageHelpers {
    public static ItemScroll.SortPref canDepositItem(Level level, BlockEntity tile, ItemStack stack) {
        ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
        if (tile == null || stack == null || stack.isEmpty())
            return ItemScroll.SortPref.INVALID;

        IItemHandler handler = getItemCapFromTile(tile);
        if (handler == null)
            return ItemScroll.SortPref.INVALID;

        for (ItemFrame itemFrame : level.getEntitiesOfClass(ItemFrame.class, new AABB(tile.getBlockPos()).inflate(1))) {

            BlockPos framePos = itemFrame.blockPosition();
            Direction facing = itemFrame.getDirection();

            BlockPos attachedBlockPos = framePos.relative(facing.getOpposite());
            BlockEntity adjTile = level.getBlockEntity(attachedBlockPos);

            if (adjTile != null && adjTile.equals(tile)) {
                ItemStack stackInFrame = itemFrame.getItem();

                if (stackInFrame.isEmpty())
                    continue;

                if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                    pref = scrollItem.getSortPref(stack, stackInFrame, handler);
                } else if (stackInFrame.getItem().equals(stack.getItem())) {
                    pref = ItemScroll.SortPref.HIGHEST;
                } else {
                    return ItemScroll.SortPref.INVALID;
                }
            }
        }

        return !ItemStack.matches(ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true), stack) ? pref : ItemScroll.SortPref.INVALID;
    }

    public static BlockPos getValidStorePos(Level level, List<BlockPos> containerPosTargets, ItemStack stack) {
        if (containerPosTargets.isEmpty() || stack.isEmpty())
            return null;
        BlockPos returnPos = null;
        ItemScroll.SortPref foundPref = ItemScroll.SortPref.INVALID;

        for (BlockPos b : containerPosTargets) {
            ItemScroll.SortPref pref = sortPrefForStack(level, b, stack);
            // Pick our highest priority
            if (pref.ordinal() > foundPref.ordinal()) {
                foundPref = pref;
                returnPos = b;
                if (foundPref == ItemScroll.SortPref.HIGHEST) {
                    return returnPos;
                }
            }
        }
        return returnPos;
    }

    public static List<BlockPos> getValidStorePositions(Level level, List<BlockPos> containerPosTargets, ItemStack stack) {
        List<BlockPos> validPositions = new ArrayList<>();
        for (BlockPos pos : containerPosTargets) {
            if (sortPrefForStack(level, pos, stack) != ItemScroll.SortPref.INVALID) {
                validPositions.add(pos);
            }
        }
        return validPositions;
    }

    public static ItemScroll.SortPref sortPrefForStack(Level level, @Nullable BlockPos b, ItemStack stack) {
        if (stack == null || stack.isEmpty() || b == null || !level.isLoaded(b))
            return ItemScroll.SortPref.INVALID;
        return canDepositItem(level, level.getBlockEntity(b), stack);
    }

    public static @Nullable IItemHandler getItemCapFromTile(BlockEntity blockEntity) {
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            var lazy = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
            if (lazy.isPresent())
                return lazy.get();
        }
        return null;
    }

}
