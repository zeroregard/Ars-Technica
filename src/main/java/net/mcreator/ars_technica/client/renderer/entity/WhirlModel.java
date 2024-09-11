package net.mcreator.ars_technica.client.renderer.entity;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public class WhirlModel extends GeoModel<WhirlEntity> {
    private FanProcessingType type;

    public void setType(FanProcessingType type) {
        this.type = type;
    }

    @Override
    public void setCustomAnimations(WhirlEntity entity, long uniqueID, @Nullable AnimationState<WhirlEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(WhirlEntity whirl) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "geo/whirl.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WhirlEntity whirl) {
        String suffix = "";
        if (type == AllFanProcessingTypes.BLASTING) {
            suffix = "_blast";
        } else if (type == AllFanProcessingTypes.HAUNTING) {
            suffix = "_haunt";
        } else if (type == AllFanProcessingTypes.SMOKING) {
            suffix = "_smoke";
        } else if (type == AllFanProcessingTypes.SPLASHING) {
            suffix = "_wash";
        }
        String path = "textures/entity/whirl" + suffix + ".png";
        return new ResourceLocation(ArsTechnicaMod.MODID, path);
    }

    @Override
    public ResourceLocation getAnimationResource(WhirlEntity whirl) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "animations/animations_whirl.json");
    }
}
