package net.mcreator.ars_technica.client.renderer.entity;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.fusion.ArcaneFusionEntity;
import net.mcreator.ars_technica.common.entity.fusion.ArcaneFusionType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

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
        return new ResourceLocation(ArsTechnicaMod.MODID, "geo/arcane_fusion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcaneFusionEntity entity) {
        String textureLocation = "textures/entity/arcane_fusion_regular.png";
        if(type != null) {
            textureLocation = type.getTextureLocation();
        }
        return new ResourceLocation(ArsTechnicaMod.MODID, textureLocation);
    }

    @Override
    public ResourceLocation getAnimationResource(ArcaneFusionEntity entity) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "animations/animations_arcane_fusion.json");
    }


}
