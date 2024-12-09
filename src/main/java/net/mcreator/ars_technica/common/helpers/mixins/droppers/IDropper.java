package net.mcreator.ars_technica.common.helpers.mixins.droppers;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface IDropper {
    /**
     * Drops the item.
     *
     * @param world  The level/world where the block is being removed.
     * @param pos    The position of the block.
     * @param state  The state of the block.
     * @param player The player performing the action.
     */
    void dropItem(Level world, BlockPos pos, BlockState state, Player player);
}