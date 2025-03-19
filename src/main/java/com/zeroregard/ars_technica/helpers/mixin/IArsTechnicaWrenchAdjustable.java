package com.zeroregard.ars_technica.helpers.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IArsTechnicaWrenchAdjustable {
    void handleWrenching(Level world, BlockPos pos, Player player);
}
