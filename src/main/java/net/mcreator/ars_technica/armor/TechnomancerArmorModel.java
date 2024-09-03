package net.mcreator.ars_technica.armor;

import com.hollingsworth.arsnouveau.ArsNouveau;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class TechnomancerArmorModel<T extends GeoAnimatable> extends GeoModel<T> {

  public ResourceLocation modelLocation;
  public ResourceLocation textLoc;
  public ResourceLocation animationLoc;

  public TechnomancerArmorModel(String name) {
    this.modelLocation = new ResourceLocation(ArsTechnicaMod.MODID, "geo/" + name + ".geo.json");
    this.textLoc = new ResourceLocation(ArsTechnicaMod.MODID, "textures/armor/" + name + ".png");
  }

  public TechnomancerArmorModel<T> withEmptyAnim() {
    this.animationLoc = new ResourceLocation(ArsNouveau.MODID, "animations/empty.json");
    return this;
  }

  @Override
  public ResourceLocation getModelResource(T object) {
    return modelLocation;
  }

  @Override
  public ResourceLocation getTextureResource(T object) {
    return textLoc;
  }

  @Override
  public ResourceLocation getAnimationResource(T animatable) {
    return this.animationLoc;
  }

}