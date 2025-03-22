package com.zeroregard.ars_technica.client.gui;

import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.zeroregard.ars_technica.Config;
import com.zeroregard.ars_technica.api.IModifiableCooldown;
import com.zeroregard.ars_technica.network.CustomCooldownPacket;

public class RelayTileScreen extends CooldownScreen<RelayTile> {
    public RelayTileScreen(String title, RelayTile be) {
        super(title, be, Config.Common.RELAY_MIN_COOLDOWN_VALUE.get(), Config.Common.RELAY_MAX_COOLDOWN_VALUE.get());
    }

    @Override
    protected void updateEntity(Integer sliderValue) {
        if (blockEntity instanceof IModifiableCooldown customizable) {
            customizable.setCooldownTicks(sliderValue);
        }
    }

    @Override
    protected int getInitialEntityStateValue() {
        if (blockEntity instanceof IModifiableCooldown customizable) {
            return customizable.getCooldownTicks();
        }
        return 20;
    }


    @Override
    protected void send(int value) {
        CustomCooldownPacket packet = new CustomCooldownPacket(value, blockEntity.getBlockPos());
        Networking.sendToServer(packet);
    }
}
