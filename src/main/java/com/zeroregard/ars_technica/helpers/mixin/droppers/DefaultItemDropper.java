package com.zeroregard.ars_technica.helpers.mixin.droppers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultItemDropper implements IDropper {
    public static final DefaultItemDropper INSTANCE = new DefaultItemDropper();

    private DefaultItemDropper() {}

    @Override
    public void dropItem(Level world, BlockPos pos, BlockState state, Player player) {
        ItemStack itemToGive = new ItemStack(state.getBlock().asItem());
        player.getInventory().add(itemToGive);
    }
}