package net.mcreator.ars_technica.mixin.Relay;

import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import net.mcreator.ars_technica.common.helpers.mixins.IAverageTransferRateAccessor;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RelayTile.class)
public class RelayTileMixin {

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    private void modifyGetTooltip(List<Component> tooltip, CallbackInfo ci) {
        var accessor = (IAverageTransferRateAccessor) this;
        int averageTransferRate = accessor.getAverageTransferRatePerSecond();
        tooltip.add(Component.translatable("ars_nouveau.relay.average_transfer_rate", averageTransferRate));
    }
}