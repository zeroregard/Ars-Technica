package net.mcreator.ars_technica.mixin.Relay;

import com.hollingsworth.arsnouveau.common.block.Relay;
import com.hollingsworth.arsnouveau.common.block.RelaySplitter;
import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.mcreator.ars_technica.client.gui.RelayTileScreen;
import net.mcreator.ars_technica.common.helpers.mixins.IArsTechnicaWrenchAdjustable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RelaySplitter.class)
public abstract class SplitterRelayBlockMixin implements IArsTechnicaWrenchAdjustable {
    public void openAdjustmentGUI(Level world, BlockPos pos, Player player) {
        if (world.getBlockEntity(pos) instanceof RelaySplitterTile relayTile) {
            displayScreen(relayTile, player);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 0.25f, 1.0f);
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(RelayTile be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new RelayTileScreen("gui.ars_technica.relay", be));
    }
}