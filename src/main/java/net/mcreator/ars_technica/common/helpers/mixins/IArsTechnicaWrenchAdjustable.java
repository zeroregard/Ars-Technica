package net.mcreator.ars_technica.common.helpers.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IArsTechnicaWrenchAdjustable {
    void handleWrenching(Level world, BlockPos pos, Player player);
}
