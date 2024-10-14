package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BasicSpellTurret.class)
public abstract class BasicSpellTurretMixin implements EncasableBlock {

}