package net.mcreator.ars_technica.client.renderer.entity;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ArcaneHammerEntity;
import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public class ArcaneHammerModel extends GeoModel<ArcaneHammerEntity> {

    @Override
    public void setCustomAnimations(ArcaneHammerEntity entity, long uniqueID, @Nullable AnimationState<ArcaneHammerEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcaneHammerEntity entity) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "geo/arcane_hammer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcaneHammerEntity entity) {
        String path = "textures/entity/arcane_hammer.png";
        return new ResourceLocation(ArsTechnicaMod.MODID, path);
    }

    @Override
    public ResourceLocation getAnimationResource(ArcaneHammerEntity entity) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "animations/animations_arcane_hammer.json");
    }


}
