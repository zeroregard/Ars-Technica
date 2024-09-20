package net.mcreator.ars_technica.common.items.equipment;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;

public class SpyMonocleModel extends BakedModelWrapper<BakedModel> {

    public SpyMonocleModel(BakedModel template) {
        super(template);
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraItemDisplayContext, PoseStack mat, boolean leftHanded) {
        if (cameraItemDisplayContext == ItemDisplayContext.HEAD)
            return new PartialModel(new ResourceLocation(ArsTechnicaMod.MODID, "block/spy_monocle")).get()
                    .applyTransform(cameraItemDisplayContext, mat, leftHanded);
        return super.applyTransform(cameraItemDisplayContext, mat, leftHanded);
    }

}