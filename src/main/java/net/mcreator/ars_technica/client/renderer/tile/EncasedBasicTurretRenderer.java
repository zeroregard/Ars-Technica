package net.mcreator.ars_technica.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.ArsGeoBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.blocks.turrets.EncasedTurretBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class EncasedBasicTurretRenderer extends ArsGeoBlockRenderer<EncasedTurretBlockEntity> {
    public static GeoModel model = new GenericModel(ArsTechnicaMod.MODID, "encased_basic_spell_turret");
    private final GeoModel encasing = new GenericModel(ArsTechnicaMod.MODID, "spell_turret_encasing").withEmptyAnim();

    public EncasedBasicTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public EncasedBasicTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, GeoModel<EncasedTurretBlockEntity> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EncasedTurretBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        Direction direction = animatable.getBlockState().getValue(BasicSpellTurret.FACING);
        if (direction == Direction.UP) {
            poseStack.translate(0, 0.5, -0.5);
        } else if (direction == Direction.DOWN) {
            poseStack.translate(0, 0.5, 0.5);
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
