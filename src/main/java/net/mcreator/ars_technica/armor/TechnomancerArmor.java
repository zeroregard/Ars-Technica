package net.mcreator.ars_technica.armor;

import net.minecraft.world.item.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.ArmorRenderer;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.renderer.item.TechnomancerArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TechnomancerArmor extends AnimatedMagicArmor {

  public TechnomancerArmor(ArmorItem.Type slot) {
    super(TechnomancerMaterial.INSTANCE, slot, new TechnomancerArmorModel("technomancer_medium_armor").withEmptyAnim());
  }

  @Override
  public int getMinTier() {
    return 2;
  }

  @Override
  public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    String color = getColor(stack);
    String location = "textures/armor/technomancer_medium_armor_" + color + ".png";
    return new ResourceLocation(ArsTechnicaMod.MODID, location).toString();
  }

  @Override
  public String getColor(ItemStack object) {
    IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(object);
    if (!(perkHolder instanceof ArmorPerkHolder data)) {
      return "brown";
    }
    return data.getColor() == null || data.getColor().isEmpty() ? "brown" : data.getColor();
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    super.initializeClient(consumer);
    consumer.accept(new IClientItemExtensions() {
      private GeoArmorRenderer<?> renderer;

      @Override
      public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
          EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        if (renderer == null) {
          renderer = new TechnomancerArmorRenderer(getArmorModel());
        }
        renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
        return this.renderer;
      }
    });
  }
}