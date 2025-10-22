package com.zeroregard.ars_technica.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class TransmutationFocusRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (slotContext.entity() instanceof Player player) {
            matrixStack.pushPose();
            
            // Position the focus floating around the player's head
            double bob = 0.05 * Math.sin(ageInTicks / 10);
            double eyeY = player.getEyeY() + 0.2 + bob;
            
            // Calculate position around the player's head
            double angle = Math.toRadians(player.yBodyRot + ageInTicks * 2);
            double radius = 0.5;
            double x = Math.sin(angle) * radius;
            double z = -Math.cos(angle) * radius;
            
            matrixStack.translate(x, eyeY - player.getY(), z);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.mulPose(Axis.YP.rotationDegrees(ageInTicks * 2));
            
            // Render the item
            Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, 
                ItemDisplayContext.FIXED, 
                light, 
                OverlayTexture.NO_OVERLAY, 
                matrixStack, 
                buffers, 
                player.level(), 
                0
            );
            
            matrixStack.popPose();
        }
    }
}