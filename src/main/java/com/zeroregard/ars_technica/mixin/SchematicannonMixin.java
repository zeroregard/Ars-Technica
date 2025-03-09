package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.zeroregard.ars_technica.Config;
import com.zeroregard.ars_technica.armor.TechnomancerArmor;
import net.minecraft.server.level.ServerPlayer;
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

    @Shadow(remap = false)
    private int printerCooldown;

    @Shadow(remap = false)
    public SchematicannonBlockEntity.State state;

    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    public void modifyCooldownEveryTick(CallbackInfo ci) {
        if(!Config.Common.SCHEMATIC_CANNON_SPEED_BOOST_ENABLED.get()) {
            return;
        }

        SchematicannonBlockEntity entity = (SchematicannonBlockEntity) (Object) this;
        double range = Config.Common.SCHEMATIC_CANNON_SPEED_BOOST_RANGE.get();
        AABB aabb = new AABB(entity.getBlockPos()).inflate(range);
        Level world = entity.getLevel();
        List<ServerPlayer> nearbyPlayers = world.getEntitiesOfClass(ServerPlayer.class, aabb);
        boolean technomancerNearby = nearbyPlayers.stream().anyMatch(TechnomancerArmor::isWearingFullSet);

        if(technomancerNearby && world.getGameTime() % 8 == 0) {
            sendBoostParticles(entity, nearbyPlayers);
        }

        if(state != SchematicannonBlockEntity.State.RUNNING) {
            return;
        }

        if (technomancerNearby) {
            boolean subtractCooldown = world.getGameTime() % 2 == 0;
            if (printerCooldown > 0 && subtractCooldown) {
                printerCooldown--;
            }
        }
    }

    private void sendBoostParticles(SchematicannonBlockEntity entity, List<ServerPlayer> players) {
        ParticleColor color = ParticleColor.PURPLE;
        for (ServerPlayer player : players) {
            // ParticleEffectPacket packet = new ParticleEffectPacket(entity.getBlockPos().getCenter(), ModParticles.SPIRAL_DUST_TYPE.get(), color);
            // NetworkHandler.CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}