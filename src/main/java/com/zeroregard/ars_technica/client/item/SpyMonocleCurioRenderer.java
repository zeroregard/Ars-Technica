package com.zeroregard.ars_technica.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

@OnlyIn(Dist.CLIENT)
public class SpyMonocleCurioRenderer implements ICurioRenderer {
    public static final ModelLayerLocation SPY_MONOCLE_LAYER = new ModelLayerLocation(prefix("spy_monocle"), "monocle");
    private final HumanoidModel<LivingEntity> model;

    public SpyMonocleCurioRenderer(ModelPart part)
    {
        this.model = new HumanoidModel<>(part);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
        matrixStack.pushPose();
        if (slotContext.entity().isCrouching()) {
            matrixStack.translate(0.0F, 0.26F, 0.0F);
        }
        var resourceLoc = prefix("textures/entity/spy_monocle.png");
        var vertexConsumer = renderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(resourceLoc));
        model.head.render(matrixStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
        PartDefinition partdefinition = mesh.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(7, 0).addBox(-3.0F, -4.9F, -4.5F, 2.0F, 2.0F, 0.5F, new CubeDeformation(0.0F))
                .texOffs(8, 14).addBox(-2.9F, -4.8F, -4.7F, 1.8F, 1.8F, 0.2F, new CubeDeformation(0.0F))
                .texOffs(11, 14).addBox(-2.8F, -4.7F, -4.9F, 1.6F, 1.6F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(7, 7).addBox(-0.2F, -0.2F, 0.3F, 1.0F, 1.0F, 0.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.3F, -5.4F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(mesh, 16, 16);
    }
}