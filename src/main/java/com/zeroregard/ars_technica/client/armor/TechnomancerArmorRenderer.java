package com.zeroregard.ars_technica.client.armor;

import com.hollingsworth.arsnouveau.client.renderer.item.ArmorRenderer;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TechnomancerArmorRenderer extends ArmorRenderer {
  public TechnomancerArmorRenderer(GeoModel<AnimatedMagicArmor> modelProvider) {
    super(modelProvider);
  }

  @Override
  public ResourceLocation getTextureLocation(AnimatedMagicArmor instance) {
    return ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "textures/armor/technomancer_medium_armor_" + instance.getColor(getCurrentStack()) + ".png");
  }
}