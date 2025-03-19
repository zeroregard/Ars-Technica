package com.zeroregard.ars_technica.client.gui;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.zeroregard.ars_technica.Config;
import com.zeroregard.ars_technica.api.IRuneTileModifier;
import com.zeroregard.ars_technica.network.TicksUntilChargePacket;

public class RuneTileScreen extends CooldownScreen<RuneTile> {
    public RuneTileScreen(RuneTile be) {
        super("gui.ars_technica.rune", be, Config.Common.RUNE_MIN_COOLDOWN_VALUE.get(), Config.Common.RUNE_MAX_COOLDOWN_VALUE.get());
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
        TicksUntilChargePacket packet = new TicksUntilChargePacket(value, blockEntity.getBlockPos());
        Networking.sendToServer(packet);
    }
}
