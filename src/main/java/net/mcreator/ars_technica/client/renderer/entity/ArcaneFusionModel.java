package net.mcreator.ars_technica.client.renderer.entity;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ArcaneFusionEntity;
import net.mcreator.ars_technica.common.entity.ArcaneHammerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public class ArcaneFusionModel extends GeoModel<ArcaneFusionEntity> {

    @Override
    public void setCustomAnimations(ArcaneFusionEntity entity, long uniqueID, @Nullable AnimationState<ArcaneFusionEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcaneFusionEntity entity) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "geo/arcane_fusion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcaneFusionEntity entity) {
        String path = "textures/entity/arcane_fusion.png";
        return new ResourceLocation(ArsTechnicaMod.MODID, path);
    }

    @Override
    public ResourceLocation getAnimationResource(ArcaneFusionEntity entity) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "animations/animations_arcane_fusion.json");
    }


}
