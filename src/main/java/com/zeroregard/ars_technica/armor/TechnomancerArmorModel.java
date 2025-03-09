package com.zeroregard.ars_technica.armor;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;


public class TechnomancerArmorModel<T extends GeoItem> extends GeoModel<T> {

  public ResourceLocation modelLocation;
  public ResourceLocation textLoc;
  public ResourceLocation animationLoc;

  public TechnomancerArmorModel(String name) {
    this.modelLocation = ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "geo/" + name + ".geo.json");
    this.textLoc = ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "textures/armor/" + name + ".png");
  }

  @Override
  public ResourceLocation getModelResource(T object) {
    return modelLocation;
  }

  @Override
  public ResourceLocation getTextureResource(T object) {
    return textLoc;
  }

  public GeoModel<T> withEmptyAnim() {
    this.animationLoc = ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "animations/empty.json");
    return this;
  }

  @Override
  public ResourceLocation getAnimationResource(T animatable) {
    return this.animationLoc;
  }

}