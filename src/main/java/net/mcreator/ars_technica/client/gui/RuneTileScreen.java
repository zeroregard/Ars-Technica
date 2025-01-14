package net.mcreator.ars_technica.client.gui;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import net.mcreator.ars_technica.ConfigHandler;
import net.mcreator.ars_technica.common.api.IRuneTileModifier;
import net.mcreator.ars_technica.common.packets.TicksUntilChargePacket;
import net.mcreator.ars_technica.setup.NetworkHandler;

public class RuneTileScreen extends CooldownScreen<RuneTile> {
    public RuneTileScreen(RuneTile be) {
        super("gui.ars_technica.rune", be, ConfigHandler.Common.RUNE_MIN_COOLDOWN_VALUE.get(), ConfigHandler.Common.RUNE_MAX_COOLDOWN_VALUE.get());
    }

    @Override
    protected void updateEntity(Integer sliderValue) {
        if (blockEntity instanceof IRuneTileModifier customizable) {
            customizable.setTicksUntilChargeCount(sliderValue);
        }
    }

    @Override
    protected int getInitialEntityStateValue() {
        if (blockEntity instanceof IRuneTileModifier customizable) {
            return customizable.getTicksUntilChargeCount();
        }
        return blockEntity.ticksUntilCharge;
    }

    @Override
    protected void send(int value) {
        NetworkHandler.CHANNEL.sendToServer(new TicksUntilChargePacket(value, blockEntity.getBlockPos()));
    }
}
