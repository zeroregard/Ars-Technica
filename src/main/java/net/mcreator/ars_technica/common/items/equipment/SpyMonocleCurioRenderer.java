package net.mcreator.ars_technica.common.items.equipment;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Axis;
import com.simibubi.create.Create;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.client.Minecraft;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class SpyMonocleCurioRenderer implements ICurioRenderer {
    public static final ModelLayerLocation SPY_MONOCLE_LAYER = new ModelLayerLocation(new ResourceLocation(ArsTechnicaMod.MODID, "spy_monocle"), "monocle");
    private final HumanoidModel<LivingEntity> model;

    public SpyMonocleCurioRenderer(ModelPart part) {
        this.model = new HumanoidModel<>(part);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
        ICurioRenderer.followHeadRotations(slotContext.entity(), model.head);

        // Translate and rotate with our head
        matrixStack.pushPose();
        matrixStack.translate(model.head.x, model.head.y, model.head.z);
        matrixStack.translate(0.125, -0.125, -0.75);
        matrixStack.mulPose(Axis.YP.rotation(model.head.yRot));
        matrixStack.mulPose(Axis.XP.rotation(model.head.xRot));

        // Translate and scale to our head
        matrixStack.translate(0.125, -0.125, -0.75);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0f));

        // Render
        model.head.render(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(new ResourceLocation(Create.ID, "textures/block/brass_casing.png"))), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createSpyMonocleModel(), 1, 1);
    }

    public static MeshDefinition createSpyMonocleModel() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
        CubeListBuilder builder = CubeListBuilder.create();

        builder.addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.5F, new CubeDeformation(0.0F));

        builder.addBox(0.1F, 0.1F, -0.2F, 1.8F, 1.8F, 0.2F, new CubeDeformation(0.0F));

        builder.addBox(0.2F, 0.2F, -0.4F, 1.6F, 1.6F, 0.2F, new CubeDeformation(0.0F));

        builder.addBox(0.2F, 0.6F, -0.6F, 1.0F, 1.0F, 0.2F, new CubeDeformation(0.0F));

        mesh.getRoot().addOrReplaceChild("head", builder, PartPose.ZERO);

        return mesh;
    }
}