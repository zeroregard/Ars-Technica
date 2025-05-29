package com.zeroregard.ars_technica.helpers.mixin.droppers;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RelayDropper implements IDropper {
    public static final RelayDropper INSTANCE = new RelayDropper();

    private RelayDropper() {}

    @Override
    public void dropItem(Level world, BlockPos pos, BlockState state, Player player) {
        // PreciseRelay should drop a regular Relay item when broken with wrench
        ItemStack itemToGive = new ItemStack(BlockRegistry.RELAY.asItem());
        player.getInventory().add(itemToGive);
    }
} 