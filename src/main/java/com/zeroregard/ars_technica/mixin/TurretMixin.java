package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.zeroregard.ars_technica.helpers.mixin.ArsTechnicaWrenchable;
import com.zeroregard.ars_technica.helpers.mixin.droppers.DefaultItemDropper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BasicSpellTurret.class)
public abstract class TurretMixin implements IWrenchable {
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return ArsTechnicaWrenchable.onSneakWrenched(state, context, DefaultItemDropper.INSTANCE);
    }
}