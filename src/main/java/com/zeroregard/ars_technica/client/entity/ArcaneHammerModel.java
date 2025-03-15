package com.zeroregard.ars_technica.client.entity;

import com.zeroregard.ars_technica.entity.ArcaneHammerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class ArcaneHammerModel extends GeoModel<ArcaneHammerEntity> {

    @Override
    public void setCustomAnimations(ArcaneHammerEntity entity, long uniqueID, @Nullable AnimationState<ArcaneHammerEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcaneHammerEntity entity) {
        return prefix("geo/arcane_hammer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcaneHammerEntity entity) {
        return prefix("textures/entity/arcane_hammer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArcaneHammerEntity entity) {
        return prefix("animations/animations_arcane_hammer.json");
    }


}
