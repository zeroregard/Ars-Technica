package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WhirlEntityRenderer extends EntityRenderer<WhirlEntity> {

    public WhirlEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(WhirlEntity entity) {
        // No texture for now
        return null;
    }

    @Override
    public void render(WhirlEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, net.minecraft.client.renderer.@NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        double posX = entity.getX();
        double posY = entity.getY();
        double posZ = entity.getZ();

        ArsTechnicaMod.LOGGER.info(entity.getPosition(0).toString());

        particleEngine.createParticle(ParticleTypes.FLAME, posX, posY , posZ, 0.0, 0.1, 0.0);
    }

}
