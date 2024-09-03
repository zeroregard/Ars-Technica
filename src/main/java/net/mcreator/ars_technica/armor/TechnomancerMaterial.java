package net.mcreator.ars_technica.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ArmorMaterial;
import org.jetbrains.annotations.NotNull;

public class TechnomancerMaterial implements ArmorMaterial {

  public static final TechnomancerMaterial INSTANCE = new TechnomancerMaterial();

  @Override
  public int getDurabilityForType(ArmorItem.Type type) {
    return switch (type) {
      case HELMET -> 15;
      case CHESTPLATE -> 17;
      case LEGGINGS -> 18;
      case BOOTS -> 13;
    };
  }

  @Override
  public int getDefenseForType(ArmorItem.Type type) {
    return switch (type) {
      case HELMET -> 3;
      case CHESTPLATE -> 6;
      case LEGGINGS -> 8;
      case BOOTS -> 3;
    };
  }

  @Override
  public Ingredient getRepairIngredient() {
    return Ingredient.EMPTY;
  }

  @Override
  public String getName() {
    return "ars_artificy:technomancer";
  }

  @Override
  public @NotNull SoundEvent getEquipSound() {
    return SoundEvents.ARMOR_EQUIP_NETHERITE;
  }

  @Override
  public int getEnchantmentValue() {
    return 40;
  }

  @Override
  public float getToughness() {
    return 2.0F;
  }

  @Override
  public float getKnockbackResistance() {
    return 0.0F;
  }
}