package com.zeroregard.ars_technica.armor;

import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ATMaterials {

  public static final DeferredRegister<ArmorMaterial> A_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, ArsTechnica.MODID);

  public static final EnumMap<ArmorItem.Type, Integer> ARMOR_SLOT_PROTECTION = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
    map.put(ArmorItem.Type.BOOTS, 2);
    map.put(ArmorItem.Type.LEGGINGS, 5);
    map.put(ArmorItem.Type.CHESTPLATE, 6);
    map.put(ArmorItem.Type.HELMET, 2);
    map.put(ArmorItem.Type.BODY, 4);
  });

  public final static Holder<ArmorMaterial> techno = A_MATERIALS.register("medium_techno", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION
          , 40, new Holder.Direct<>(SoundEvents.ARMOR_EQUIP_NETHERITE.value()), () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsTechnica.prefix("medium_techno"))), 2.0f, 0));
}