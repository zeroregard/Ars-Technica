package com.zeroregard.ars_technica.block;

import com.hollingsworth.arsnouveau.common.block.Relay;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.zeroregard.ars_technica.client.gui.RelayTileScreen;
import com.zeroregard.ars_technica.helpers.mixin.IArsTechnicaWrenchAdjustable;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

public class PreciseRelay extends Relay implements IArsTechnicaWrenchAdjustable {

    public PreciseRelay(Properties properties) {
        super(properties);
    }

    public void handleWrenching(Level world, BlockPos pos, Player player) {
        if (world.getBlockEntity(pos) instanceof RelayTile relayTile) {
            if(world.isClientSide()) {
                displayScreen(relayTile, player);
            }
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 0.25f, 1.0f);
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(RelayTile be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new RelayTileScreen("gui.ars_technica.relay", be));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        var tile = new PreciseRelayTile(pos, state);
        return tile;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(BlockRegistry.RELAY.asItem()));
    }


}
