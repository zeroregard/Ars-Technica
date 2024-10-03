package net.mcreator.ars_technica.common.blocks;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.mcreator.ars_technica.ConfigHandler;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.setup.BlockRegistry;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SourceEngineBlockEntity extends GeneratingKineticBlockEntity {

    public static final int DEFAULT_SPEED = 16;
    public static final int MAX_SPEED = 256;

    protected boolean fueled = false;
    protected ScrollValueBehaviour generatedSpeed;
    protected int tickCount = 0;


    public boolean isFueled() {
        return fueled;
    }

    public SourceEngineBlockEntity(BlockPos pos, BlockState state) {
        super(EntityRegistry.SOURCE_ENGINE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = MAX_SPEED;
        generatedSpeed = new KineticScrollValueBehaviour(Lang.translateDirect("kinetics.creative_motor.rotation_speed"),
                this, new MotorValueBox());
        generatedSpeed.between(-max, max);
        generatedSpeed.value = DEFAULT_SPEED;
        generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
        behaviours.add(generatedSpeed);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (!IRotate.StressImpact.isEnabled())
            return added;

        Lang.translate("gui.goggles.source_consumption")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        int sourceCostTotal = getSourceCost();

        Lang.number(sourceCostTotal)
                .space()
                .translate("ars_nouveau.unit.source")
                .style(ChatFormatting.DARK_PURPLE)
                .space()
                .add(Lang.translate("gui.goggles.per_second")
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        return true;
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;
        if (tickCount % 20 == 0) {
            consumeSource();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    protected void consumeSource() {
        var sourceCost = getSourceCost();
        var success = SourceUtil.takeSourceWithParticles(worldPosition, level, 10, sourceCost) != null;
        var fueledStateChanged = success != fueled;
        fueled = success;
        if(fueledStateChanged) {
            updateGeneratedRotation();
            var pos = getBlockPos().getCenter();
            var event = fueled ? ArsTechnicaModSounds.SOURCE_ENGINE_START.get() : ArsTechnicaModSounds.SOURCE_ENGINE_STOP.get();
            getLevel().playSound(null, pos.x, pos.y, pos.z, event, SoundSource.BLOCKS, 0.25f, 1.0f);
        }
    }

    private int getSourceCost() {
        var absoluteSpeed = Math.abs(generatedSpeed.getValue());
        var sourceCost = (int)Math.round(absoluteSpeed * ConfigHandler.Common.SOURCE_MOTOR_SPEED_TO_SOURCE_MULTIPLIER.get());
        return sourceCost;
    }

    @Override
    public float getGeneratedSpeed() {
        if (!fueled) {
            return 0;
        }
        if (!BlockRegistry.SOURCE_ENGINE.get().defaultBlockState().is(getBlockState().getBlock()))
            return 0;
        return convertToDirection(generatedSpeed.getValue(), getBlockState().getValue(SourceEngineBlock.FACING));
    }

    class MotorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 12.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(SourceEngineBlock.FACING);
            return super.getLocalOffset(state).add(Vec3.atLowerCornerOf(facing.getNormal())
                    .scale(-1 / 16f));
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            super.rotate(state, ms);
            Direction facing = state.getValue(SourceEngineBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return;
            if (getSide() != Direction.UP)
                return;
            TransformStack.cast(ms)
                    .rotateZ(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.getValue(SourceEngineBlock.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN)
                return false;
            return direction.getAxis() != facing.getAxis();
        }

    }

}