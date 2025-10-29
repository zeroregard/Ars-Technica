package com.zeroregard.ars_technica.registry;


import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.client.particles.SpiralDustParticleProvider;
import com.zeroregard.ars_technica.client.particles.SpiralDustParticleTypeData;
import com.zeroregard.ars_technica.client.particles.SpiralDustParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ArsTechnica.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<SpiralDustParticleTypeData>> SPIRAL_DUST_TYPE = PARTICLES.register("spiral_dust", SpiralDustParticleType::new);

    @SubscribeEvent
    public void registerFactories(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(SPIRAL_DUST_TYPE.get(), SpiralDustParticleProvider::new);
    }
}