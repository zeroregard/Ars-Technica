package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.utility.Lang;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.gui.SourceEngineScreen;
import net.mcreator.ars_technica.common.blocks.SourceEngineBlockEntity;
import net.mcreator.ars_technica.common.helpers.CooldownHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mcreator.ars_technica.common.api.IRuneTileModifier;

import java.util.List;

@Mixin(RuneTile.class)
public class RuneTileMixin implements IRuneTileModifier, IHaveGoggleInformation {

    @Shadow(remap = false)
    private int ticksUntilCharge;

    // Normally this is hardcoded to 20 * 2 or 20 * 3 but here we introduce
    // this value to be able to customize it per rune
    private int ticksUntilChargeCount = 0;

    @Inject(method = "castSpell", at = @At("TAIL"), remap = false)
    private void modifyTicksUntilCharge(Entity entity, CallbackInfo ci) {
        if (entity == null) return;

        if (ticksUntilChargeCount > 0) {
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
        Lang.text("Cooldown: ").forGoggles(tooltip);
        int ticksUntilCharge = ticksUntilChargeCount == 0 ? 20 * 2 : ticksUntilChargeCount;
        Lang.text(CooldownHelper.getCooldownText(ticksUntilCharge)).forGoggles(tooltip);
        return true;
    }

    @Override
    public void setTicksUntilChargeCount(int ticks) {
        this.ticksUntilChargeCount = ticks;
    }

    @Override
    public int getTicksUntilChargeCount() {
        return this.ticksUntilChargeCount;
    }

}