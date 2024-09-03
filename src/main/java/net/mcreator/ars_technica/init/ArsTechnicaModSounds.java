
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.ars_technica.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.ars_technica.ArsTechnicaMod;

public class ArsTechnicaModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ArsTechnicaMod.MODID);
	public static final RegistryObject<SoundEvent> EQUIP_SET = REGISTRY.register("equip_set", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ars_technica", "equip_set")));
}
