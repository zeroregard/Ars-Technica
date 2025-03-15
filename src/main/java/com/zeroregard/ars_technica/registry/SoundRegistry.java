
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.zeroregard.ars_technica.registry;

import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;


public class SoundRegistry {
	public static final DeferredRegister<SoundEvent> SOUNDS =
			DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ArsTechnica.MODID);

	private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name, SoundEvent soundEvent) {
		return SOUNDS.register(name, () -> soundEvent);
	}

	public static final DeferredHolder<SoundEvent, SoundEvent> CLICK = registerSound("click",
			SoundEvent.createVariableRangeEvent(prefix("click")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SPY_MONOCLE_USE = registerSound("spy_monocle_use",
			SoundEvent.createVariableRangeEvent(prefix("spy_monocle_use")));

	public static final DeferredHolder<SoundEvent, SoundEvent> WHIRL_NONE = registerSound("whirl_none",
			SoundEvent.createVariableRangeEvent(prefix("whirl_none")));
	public static final DeferredHolder<SoundEvent, SoundEvent> WHIRL_SMELT = registerSound("whirl_smelt",
			SoundEvent.createVariableRangeEvent(prefix("whirl_smelt")));
	public static final DeferredHolder<SoundEvent, SoundEvent> WHIRL_SPLASH = registerSound("whirl_splash",
			SoundEvent.createVariableRangeEvent(prefix("whirl_splash")));
	public static final DeferredHolder<SoundEvent, SoundEvent> WHIRL_HAUNT = registerSound("whirl_haunt",
			SoundEvent.createVariableRangeEvent(prefix("whirl_haunt")));
	public static final DeferredHolder<SoundEvent, SoundEvent> WHIRL_PROCESS_AFFECT_WATER = registerSound("whirl_process_affect_water",
			SoundEvent.createFixedRangeEvent(prefix("whirl_process_affect_water"), 8f));
	public static final DeferredHolder<SoundEvent, SoundEvent> WHIRL_PROCESS_AFFECT_FIRE = registerSound("whirl_process_affect_fire",
			SoundEvent.createFixedRangeEvent(prefix("whirl_process_affect_fire"), 8f));

	public static final DeferredHolder<SoundEvent, SoundEvent> OBLITERATE_CHARGE = registerSound("obliterate_charge",
			SoundEvent.createFixedRangeEvent(prefix("obliterate_charge"), 16f));
	public static final DeferredHolder<SoundEvent, SoundEvent> OBLITERATE_CHARGE_LARGE = registerSound("obliterate_charge_large",
			SoundEvent.createFixedRangeEvent(prefix("obliterate_charge_large"), 16f));
	public static final DeferredHolder<SoundEvent, SoundEvent> OBLITERATE_SMASH = registerSound("obliterate_smash",
			SoundEvent.createFixedRangeEvent(prefix("obliterate_smash"), 16f));
	public static final DeferredHolder<SoundEvent, SoundEvent> OBLITERATE_SWING = registerSound("obliterate_swing",
			SoundEvent.createFixedRangeEvent(prefix("obliterate_swing"), 16f));
	public static final DeferredHolder<SoundEvent, SoundEvent> OBLITERATE_SHOCKWAVE = registerSound("obliterate_shockwave",
			SoundEvent.createFixedRangeEvent(prefix("obliterate_shockwave"), 16f));

	public static final DeferredHolder<SoundEvent, SoundEvent> FUSE_CHARGE = registerSound("fuse_charge",
			SoundEvent.createFixedRangeEvent(prefix("fuse_charge"), 16f));
	public static final DeferredHolder<SoundEvent, SoundEvent> FUSE_SWING = registerSound("fuse_swing",
			SoundEvent.createFixedRangeEvent(prefix("fuse_swing"), 16f));
	public static final DeferredHolder<SoundEvent, SoundEvent> FUSE_IMPACT = registerSound("fuse_impact",
			SoundEvent.createFixedRangeEvent(prefix("fuse_impact"), 16f));
	public static final DeferredHolder<SoundEvent, SoundEvent> FUSE_FAILED = registerSound("fuse_failed",
			SoundEvent.createFixedRangeEvent(prefix("fuse_failed"), 16f));

	public static final DeferredHolder<SoundEvent, SoundEvent> SOURCE_ENGINE_START = registerSound("source_engine_start",
			SoundEvent.createFixedRangeEvent(prefix("source_engine_start"), 8f));
	public static final DeferredHolder<SoundEvent, SoundEvent> SOURCE_ENGINE_STOP = registerSound("source_engine_stop",
			SoundEvent.createFixedRangeEvent(prefix("source_engine_stop"), 8f));

	public static final DeferredHolder<SoundEvent, SoundEvent> POCKET_FACTORY_DISC = registerSound("pocket_factory",
			SoundEvent.createVariableRangeEvent(prefix("pocket_factory")));
}
