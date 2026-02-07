package com.zeroregard.ars_technica.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.equipment.wrench.WrenchItemRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.zeroregard.ars_technica.client.item.ArcaneWrenchRenderer;
import com.zeroregard.ars_technica.helpers.ArcaneWrenchHelper;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces Create's wrench rendering with Arcane Wrench rendering when the stack
 * has our arcane_wrench component. Targets WrenchItemRenderer.render() directly
 * so we replace the entire rendering in one shot (base + gear), avoiding the
 * double-render issue of intercepting individual PartialItemModelRenderer.render() calls.
 */
@OnlyIn(Dist.CLIENT)
@Mixin(value = WrenchItemRenderer.class, remap = false)
public class ArcaneWrenchPartialItemModelRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void arsTechnica$renderArcaneWrench(ItemStack stack, CustomRenderedItemModel model,
                                                 PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                                                 PoseStack ms, MultiBufferSource buffer, int light, int overlay,
                                                 CallbackInfo ci) {
        if (stack.isEmpty() || !ArcaneWrenchHelper.isArcaneWrench(stack))
            return;

        ci.cancel();

        // Render our base wrench model (runic_spanner shape + texture)
        renderer.render(ArcaneWrenchRenderer.RUNIC_SPANNER_BASE.get(), light);

        // Render our animated gear overlay
        float xOffset = -1 / 16f;
        ms.translate(-xOffset, 0, 0);
        ms.mulPose(Axis.YP.rotationDegrees(ScrollValueHandler.getScroll(AnimationTickHolder.getPartialTicks())));
        ms.translate(xOffset, 0, 0);

        renderer.renderGlowing(ArcaneWrenchRenderer.GEAR.get(), LightTexture.FULL_BRIGHT);
    }
}
