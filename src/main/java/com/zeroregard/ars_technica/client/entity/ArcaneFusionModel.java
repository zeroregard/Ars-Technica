package com.zeroregard.ars_technica.client.entity;


import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionEntity;
import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class ArcaneFusionModel extends GeoModel<ArcaneFusionEntity> {
    private ArcaneFusionType type;

    public void setType(ArcaneFusionType type) {
        this.type = type;
    }
    @Override
    public void setCustomAnimations(ArcaneFusionEntity entity, long uniqueID, @Nullable AnimationState<ArcaneFusionEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcaneFusionEntity entity) {
        return prefix("geo/arcane_fusion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcaneFusionEntity entity) {
        String textureLocation = "textures/entity/arcane_fusion_regular.png";
        if(type != null) {
            textureLocation = type.getTextureLocation();
        }
        return prefix(textureLocation);
    }

    @Override
    public ResourceLocation getAnimationResource(ArcaneFusionEntity entity) {
        return prefix("animations/animations_arcane_fusion.json");
    }


}
