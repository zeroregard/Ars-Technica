package net.mcreator.ars_technica.mixin.Relay;

import com.hollingsworth.arsnouveau.common.block.RelaySplitter;
import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.api.IModifiableCooldown;
import net.mcreator.ars_technica.common.helpers.mixins.IAverageTransferRateAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.mcreator.ars_technica.common.helpers.CooldownHelper.getCooldownText;

@Mixin(RelaySplitterTile.class)
public class SplitterRelayTileMixin implements IModifiableCooldown {

    private int splitterCustomCooldownTicks = 0;

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    private void modifyGetTooltip(List<Component> tooltip, CallbackInfo ci) {
        var entity = (RelaySplitterTile) (Object)this;
        var cooldownTicks = getCooldownTicks();
        int transferRate = entity.getTransferRate();
        String coolDownText = getCooldownText(cooldownTicks);
        tooltip.add(Component.empty());
        Component transferRateComponent = Component.translatable("ars_nouveau.relay.transfer_rate", transferRate, coolDownText)
                .setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(transferRateComponent);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getGameTime()J"), remap = false)
    private long redirectGameTime(Level instance, Operation<Long> original) {
        if(this.splitterCustomCooldownTicks == 0) {
            return instance.getGameTime();
        }
        if(instance.getGameTime() % splitterCustomCooldownTicks == 0) {
            return 20;
        }
        return -1;
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void saveTicksUntilChargeCount(CompoundTag tag, CallbackInfo ci) {
        if(this.splitterCustomCooldownTicks != 0) {
            tag.putInt("SplitterCustomCooldown", splitterCustomCooldownTicks);
        }

    }

    @Inject(method = "load", at = @At("HEAD"))
    private void loadTicksUntilChargeCount(CompoundTag tag, CallbackInfo ci) {
        var coolDown = tag.getInt("SplitterCustomCooldown");
        if(coolDown != 0) {
            this.splitterCustomCooldownTicks = coolDown;
        }
    }

    @Override
    public void setCooldownTicks(int ticks) {
        this.splitterCustomCooldownTicks = ticks;
    }

    @Override
    public int getCooldownTicks() {
        return this.splitterCustomCooldownTicks;
    }
}