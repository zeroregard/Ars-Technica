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
    map.put(ArmorItem.Type.BOOTS, 3);
    map.put(ArmorItem.Type.LEGGINGS, 6);
    map.put(ArmorItem.Type.CHESTPLATE, 8);
    map.put(ArmorItem.Type.HELMET, 3);
    map.put(ArmorItem.Type.BODY, 4);
  });

  public static final EnumMap<ArmorItem.Type, Integer> ARMOR_SLOT_PROTECTION_L = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
    map.put(ArmorItem.Type.BOOTS, 2);
    map.put(ArmorItem.Type.LEGGINGS, 5);
    map.put(ArmorItem.Type.CHESTPLATE, 6);
    map.put(ArmorItem.Type.HELMET, 2);
    map.put(ArmorItem.Type.BODY, 4);
  });

  public static final EnumMap<ArmorItem.Type, Integer> ARMOR_SLOT_PROTECTION_H = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
    map.put(ArmorItem.Type.BOOTS, 4);
    map.put(ArmorItem.Type.LEGGINGS, 7);
    map.put(ArmorItem.Type.CHESTPLATE, 10);
    map.put(ArmorItem.Type.HELMET, 4);
    map.put(ArmorItem.Type.BODY, 4);
  });

  public final static Holder<ArmorMaterial> techno = A_MATERIALS.register("medium_techno", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION
          , 40, new Holder.Direct<>(SoundEvents.ARMOR_EQUIP_NETHERITE.value()), () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsTechnica.prefix("medium_techno"))), 2.0f, 0));

  public final static Holder<ArmorMaterial> techno_light = A_MATERIALS.register("light_techno", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION_L
          , 50, new Holder.Direct<>(SoundEvents.ARMOR_EQUIP_NETHERITE.value()), () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsTechnica.prefix("light_techno"))), 1.0f, 0));

  public final static Holder<ArmorMaterial> techno_heavy = A_MATERIALS.register("heavy_techno", () -> new ArmorMaterial(ARMOR_SLOT_PROTECTION_H
          , 30, new Holder.Direct<>(SoundEvents.ARMOR_EQUIP_NETHERITE.value()), () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ArsTechnica.prefix("heavy_techno"))), 4.0f, 0.05F));
}