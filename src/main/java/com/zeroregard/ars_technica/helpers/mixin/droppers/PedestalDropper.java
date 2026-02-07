package com.zeroregard.ars_technica.helpers.mixin.droppers;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * For Arcane Pedestal: before giving the block to the player, pop off any item
 * held on the pedestal (spawn as ItemEntity at block center), then give the block.
 */
public class PedestalDropper implements IDropper {

    public static final PedestalDropper INSTANCE = new PedestalDropper();

    private PedestalDropper() {}

    @Override
    public void dropItem(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ArcanePedestalTile tile) {
            ItemStack held = tile.getStack();
            if (held != null && !held.isEmpty()) {
                Vec3 center = pos.getCenter();
                ItemEntity itemEntity = new ItemEntity(world, center.x, center.y, center.z, held.copy());
                world.addFreshEntity(itemEntity);
                tile.setStack(ItemStack.EMPTY);
            }
        }
        // Give the block to the player (same as DefaultItemDropper)
        ItemStack blockItem = new ItemStack(state.getBlock().asItem());
        player.getInventory().add(blockItem);
    }
}
