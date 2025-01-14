package net.mcreator.ars_technica.mixin.Relay;

import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.mcreator.ars_technica.common.helpers.CooldownHelper.getCooldownText;

@Mixin(RelayTile.class)
public class RelayTileMixin implements IModifiableCooldown {

    private int customCooldownTicks = 0;

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    private void modifyGetTooltip(List<Component> tooltip, CallbackInfo ci) {
        var entity = (RelayTile) (Object)this;
        var cooldownTicks = getCooldownTicks();
        if(cooldownTicks == -1) {
            cooldownTicks = 20;
        }
        int transferRate = entity.getTransferRate();
        String coolDownText = getCooldownText(cooldownTicks);
        tooltip.add(Component.empty());
        Component transferRateComponent = Component.translatable("ars_nouveau.relay.transfer_rate", transferRate, coolDownText)
                .setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(transferRateComponent);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getGameTime()J"), remap = true)
    private long redirectGameTime(Level instance) {
        if(customCooldownTicks == -1) {
            return instance.getGameTime();
        }
        if(customCooldownTicks == 0) {
            return 20;
        }
        if(instance.getGameTime() % customCooldownTicks == 0) {
            return 20;
        }
        return -1;
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void saveTicksUntilChargeCount(CompoundTag tag, CallbackInfo ci) {
        if(customCooldownTicks != -1) {
            tag.putInt("CustomCooldown", customCooldownTicks);
        }

    }

    @Inject(method = "load", at = @At("HEAD"))
    private void loadTicksUntilChargeCount(CompoundTag tag, CallbackInfo ci) {
        var coolDown = tag.getInt("CustomCooldown");
        if(coolDown != -1) {
            this.customCooldownTicks = coolDown;
        }
    }

    @Override
    public void setCooldownTicks(int ticks) {
        this.customCooldownTicks = ticks;
    }

    @Override
    public int getCooldownTicks() {
        return this.customCooldownTicks;
    }
}