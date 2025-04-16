package net.mcreator.ars_technica.client.renderer.entity;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ArcanePolishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public class ArcanePolishModel extends GeoModel<ArcanePolishEntity> {

    @Override
    public void setCustomAnimations(ArcanePolishEntity entity, long uniqueID, @Nullable AnimationState<ArcanePolishEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcanePolishEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ArsTechnicaMod.MODID, "geo/arcane_polish.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcanePolishEntity entity) {
        String path = "textures/entity/arcane_press.png";
        return ResourceLocation.fromNamespaceAndPath(ArsTechnicaMod.MODID, path);
    }

    @Override
    public ResourceLocation getAnimationResource(ArcanePolishEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(ArsTechnicaMod.MODID, "animations/animations_arcane_polish.json");
    }
}
