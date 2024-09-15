package net.mcreator.ars_technica.mixin;

import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.mcreator.ars_technica.ConfigHandler;
import net.mcreator.ars_technica.armor.TechnomancerArmor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SchematicannonBlockEntity.class)
public class SchematicannonMixin {

    @Shadow
    private int printerCooldown;

    @Shadow
    private SchematicannonBlockEntity.State state;

    @Inject(method = "tick", at = @At("TAIL"))
    public void modifyCooldownEveryTick(CallbackInfo ci) {
        if(!ConfigHandler.Common.SCHEMATIC_CANNON_SPEED_BOOST_ENABLED.get()) {
            return;
        }
        if(state != SchematicannonBlockEntity.State.RUNNING) {
            return;
        }
        SchematicannonBlockEntity entity = (SchematicannonBlockEntity) (Object) this;
        double range = ConfigHandler.Common.SCHEMATIC_CANNON_SPEED_BOOST_RANGE.get();
        AABB aabb = new AABB(entity.getBlockPos()).inflate(range);
        Level world = entity.getLevel();
        List<Player> nearbyPlayers = world.getEntitiesOfClass(Player.class, aabb);

        if (!nearbyPlayers.isEmpty() && world.getGameTime() % 2 == 0) {
            boolean technomancerNearby = nearbyPlayers.stream().anyMatch(player -> TechnomancerArmor.isWearingFullSet(player));
            if (printerCooldown > 0 && technomancerNearby) {
                printerCooldown--;
            }
        }
    }
}