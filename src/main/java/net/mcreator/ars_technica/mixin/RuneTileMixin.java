package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.utility.CreateLang;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.api.IRuneTileModifier;
import net.mcreator.ars_technica.common.helpers.CooldownHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RuneTile.class)
public class RuneTileMixin implements IRuneTileModifier, IHaveGoggleInformation {

    @Shadow(remap = false)
    public int ticksUntilCharge;

    // Normally this is hardcoded to 20 * 2 or 20 * 3 but here we introduce
    // this value to be able to customize it per rune
    @Unique
    private int ticksUntilChargeCount = -1;

    @Inject(method = "castSpell", at = @At("TAIL"), remap = false)
    private void modifyTicksUntilCharge(Entity entity, CallbackInfo ci) {
        if (entity == null) return;
        ArsTechnicaMod.LOGGER.info(entity.level().getGameTime());
        if(ticksUntilChargeCount != -1) {
            this.ticksUntilCharge = ticksUntilChargeCount;
        }
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void saveTicksUntilChargeCount(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("ticksUntilChargeCount", ticksUntilChargeCount);
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void loadTicksUntilChargeCount(CompoundTag tag, CallbackInfo ci) {
        this.ticksUntilChargeCount = tag.getInt("ticksUntilChargeCount");
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.text("Cooldown: ").forGoggles(tooltip);
        int ticksUntilCharge = ticksUntilChargeCount == -1 ? 20 * 2 : ticksUntilChargeCount;
        CreateLang.text(CooldownHelper.getCooldownText(ticksUntilCharge)).forGoggles(tooltip);
        return true;
    }


    @Override
    public void setTicksUntilChargeCount(int ticks) {
        this.ticksUntilChargeCount = ticks;
    }

    @Override
    public int getTicksUntilChargeCount() {
        if(this.ticksUntilChargeCount == -1) {
            return 20 * 2;
        }
        return this.ticksUntilChargeCount;
    }

}