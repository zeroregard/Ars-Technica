package com.zeroregard.ars_technica.client.entity;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.zeroregard.ars_technica.entity.ArcaneWhirlEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;
import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class ArcaneWhirlModel extends GeoModel<ArcaneWhirlEntity> {
    private FanProcessingType type;

    public void setType(FanProcessingType type) {
        this.type = type;
    }

    @Override
    public void setCustomAnimations(ArcaneWhirlEntity entity, long uniqueID, @Nullable AnimationState<ArcaneWhirlEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcaneWhirlEntity whirl) {
        return prefix("geo/whirl.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcaneWhirlEntity whirl) {
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
        return prefix(path);
    }

    @Override
    public ResourceLocation getAnimationResource(ArcaneWhirlEntity whirl) {
        return prefix("animations/animations_whirl.json");
    }
}
