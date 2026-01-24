package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.Config;
import com.zeroregard.ars_technica.api.ITechnomancerAware;
import com.zeroregard.ars_technica.armor.HeavyTechnomancerArmor;
import com.zeroregard.ars_technica.armor.LightTechnomancerArmor;
import com.zeroregard.ars_technica.armor.TechnomancerArmor;
import com.zeroregard.ars_technica.helpers.CurioHelper;
import com.zeroregard.ars_technica.network.ParticleEffectPacket;
import com.zeroregard.ars_technica.network.TechnomancerNearbyPacket;
import com.zeroregard.ars_technica.registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

@Mixin(SchematicannonBlockEntity.class)
public class SchematicannonMixin implements ITechnomancerAware {

    @Shadow(remap = false)
    private int printerCooldown;

    @Shadow(remap = false)
    public SchematicannonBlockEntity.State state;

    private boolean technomancerNearby = false;

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
        boolean foundTechnomancer  = nearbyPlayers.stream().anyMatch(player ->
                isWearingAnyTechnicaSet(player) ||
                        CurioHelper.hasTaggedCurio(player, prefix("technomancer_perk"))
        );

        this.setTechnomancerNearby(foundTechnomancer);

        if(!world.isClientSide()) {
            TechnomancerNearbyPacket packet = new TechnomancerNearbyPacket(foundTechnomancer, entity.getBlockPos());
            Networking.sendToNearbyClient(world, entity.getBlockPos(), packet);
        }


        if(state != SchematicannonBlockEntity.State.RUNNING) {
            return;
        }

        if (foundTechnomancer) {
            boolean subtractCooldown = world.getGameTime() % 2 == 0;
            if (printerCooldown > 0 && subtractCooldown) {
                printerCooldown--;
            }
        }
    }

    private void sendBoostParticles(SchematicannonBlockEntity entity, Level world) {
        ParticleEffectPacket.send(world, ParticleColor.fromInt(ParticleColor.PURPLE.getColor()), ParticleRegistry.SPIRAL_DUST_TYPE.get(), entity.getBlockPos().getCenter());
    }

    @Override
    public boolean isTechnomancerNearby() {
        return technomancerNearby;
    }

    @Override
    public void setTechnomancerNearby(boolean value) {
        this.technomancerNearby = value;
    }

    private static boolean isWearingAnyTechnicaSet(ServerPlayer player) {
        return TechnomancerArmor.isWearingFullSet(player) ||
                LightTechnomancerArmor.isWearingFullSet(player) ||
                HeavyTechnomancerArmor.isWearingFullSet(player);
    }
}