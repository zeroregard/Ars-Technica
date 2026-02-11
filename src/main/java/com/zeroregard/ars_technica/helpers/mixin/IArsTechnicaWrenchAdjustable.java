package com.zeroregard.ars_technica.helpers.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/* Deprecated for now but may be usedful in the future.
   For now, we use the IWrenchable interface from Create to handle wrenching. 
   Until we have any custom blocks in Technica that need this, this is unused.
 */
public interface IArsTechnicaWrenchAdjustable {
    void handleWrenching(Level world, BlockPos pos, Player player);
}
