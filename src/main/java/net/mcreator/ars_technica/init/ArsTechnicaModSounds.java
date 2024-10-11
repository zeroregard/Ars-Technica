
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.ars_technica.init;

import com.simibubi.create.AllSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.ars_technica.ArsTechnicaMod;

public class ArsTechnicaModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ArsTechnicaMod.MODID);

	public static final RegistryObject<SoundEvent> CLICK = REGISTRY.register("click",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ars_technica", "click")));
	public static final RegistryObject<SoundEvent> SPY_MONOCLE_USE = REGISTRY.register("spy_monocle_use",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ars_technica", "spy_monocle_use")));

	public static final RegistryObject<SoundEvent> WHIRL_NONE = REGISTRY.register("whirl_none",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ars_technica", "whirl_none")));
	public static final RegistryObject<SoundEvent> WHIRL_SMELT = REGISTRY.register("whirl_smelt",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ars_technica", "whirl_smelt")));
	public static final RegistryObject<SoundEvent> WHIRL_SPLASH = REGISTRY.register("whirl_splash",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ars_technica", "whirl_splash")));
	public static final RegistryObject<SoundEvent> WHIRL_HAUNT = REGISTRY.register("whirl_haunt",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ars_technica", "whirl_haunt")));
	public static final RegistryObject<SoundEvent> WHIRL_PROCESS_AFFECT_WATER = REGISTRY.register("whirl_process_affect_water",
			() -> SoundEvent.createFixedRangeEvent(new ResourceLocation("ars_technica", "whirl_process_affect_water"), 8f));
	public static final RegistryObject<SoundEvent> WHIRL_PROCESS_AFFECT_FIRE = REGISTRY.register("whirl_process_affect_fire",
			() -> SoundEvent.createFixedRangeEvent(new ResourceLocation("ars_technica", "whirl_process_affect_fire"), 8f));

	public static final RegistryObject<SoundEvent> OBLITERATE_CHARGE = REGISTRY.register("obliterate_charge",
			() -> SoundEvent.createFixedRangeEvent(new ResourceLocation("ars_technica", "obliterate_charge"), 8f));
	public static final RegistryObject<SoundEvent> OBLITERATE_SMASH = REGISTRY.register("obliterate_smash",
			() -> SoundEvent.createFixedRangeEvent(new ResourceLocation("ars_technica", "obliterate_smash"), 8f));

	public static final RegistryObject<SoundEvent> SOURCE_ENGINE_START = REGISTRY.register("source_engine_start",
			() -> SoundEvent.createFixedRangeEvent(new ResourceLocation("ars_technica", "source_engine_start"), 8f));
	public static final RegistryObject<SoundEvent> SOURCE_ENGINE_STOP = REGISTRY.register("source_engine_stop",
			() -> SoundEvent.createFixedRangeEvent(new ResourceLocation("ars_technica", "source_engine_stop"), 8f));


}
