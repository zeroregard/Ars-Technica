package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.mcreator.ars_technica.ConfigHandler;
import net.mcreator.ars_technica.armor.TechnomancerArmor;
import net.mcreator.ars_technica.client.events.ModParticles;
import net.mcreator.ars_technica.network.ParticleEffectPacket;
import net.mcreator.ars_technica.setup.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkDirection;
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

        SchematicannonBlockEntity entity = (SchematicannonBlockEntity) (Object) this;
        double range = ConfigHandler.Common.SCHEMATIC_CANNON_SPEED_BOOST_RANGE.get();
        AABB aabb = new AABB(entity.getBlockPos()).inflate(range);
        Level world = entity.getLevel();
        List<ServerPlayer> nearbyPlayers = world.getEntitiesOfClass(ServerPlayer.class, aabb);
        boolean technomancerNearby = nearbyPlayers.stream().anyMatch(player -> TechnomancerArmor.isWearingFullSet(player));

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
            ParticleEffectPacket packet = new ParticleEffectPacket(entity.getBlockPos().getCenter(), ModParticles.SPIRAL_DUST_TYPE.get(), color);
            NetworkHandler.CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}