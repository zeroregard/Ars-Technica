package net.mcreator.ars_technica.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ArmorMaterial;
import org.jetbrains.annotations.NotNull;

public class TechnomancerMaterial implements ArmorMaterial {

  public static final TechnomancerMaterial INSTANCE = new TechnomancerMaterial();
  private static int DurabilityMultiplier = 33;

  @Override
  public int getDurabilityForType(ArmorItem.Type type) {
    return switch (type) {
      case HELMET -> 15 * DurabilityMultiplier;
      case CHESTPLATE -> 18 * DurabilityMultiplier;
      case LEGGINGS -> 17 * DurabilityMultiplier;
      case BOOTS -> 13 * DurabilityMultiplier;
    };
  }

  @Override
  public int getDefenseForType(ArmorItem.Type type) {
    return switch (type) {
      case HELMET, BOOTS -> 3;
      case CHESTPLATE -> 8;
      case LEGGINGS -> 6;
    };
  }

  @Override
  public Ingredient getRepairIngredient() {
    return Ingredient.EMPTY;
  }

  @Override
  public String getName() {
    return "ars_technica:technomancer";
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