package com.zeroregard.ars_technica.client.entity;

import com.zeroregard.ars_technica.entity.ArcanePolishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;
import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class ArcanePolishModel extends GeoModel<ArcanePolishEntity> {

    @Override
    public void setCustomAnimations(ArcanePolishEntity entity, long uniqueID, @Nullable AnimationState<ArcanePolishEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcanePolishEntity entity) {
        return prefix("geo/arcane_polish.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcanePolishEntity entity) {
        return prefix("textures/entity/arcane_press.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArcanePolishEntity entity) {
        return prefix("animations/animations_arcane_polish.json");
    }
}
