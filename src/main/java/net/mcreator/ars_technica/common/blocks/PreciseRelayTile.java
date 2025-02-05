package net.mcreator.ars_technica.common.blocks;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import net.mcreator.ars_technica.common.api.IModifiableCooldown;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

import static net.mcreator.ars_technica.common.helpers.CooldownHelper.getCooldownText;

public class PreciseRelayTile extends RelayTile implements IModifiableCooldown {
    private int customCooldownTicks = -1;
    public PreciseRelayTile(BlockPos pos, BlockState state) {
        super(EntityRegistry.PRECISE_RELAY_TILE.get(), pos, state);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        var cooldownTicks = getCooldownTicks();
        if(cooldownTicks == -1) {
            cooldownTicks = 20;
        }
        int transferRate = getTransferRate();
        String coolDownText = getCooldownText(cooldownTicks);
        tooltip.add(Component.empty());
        Component transferRateComponent = Component.translatable("ars_nouveau.relay.transfer_rate", transferRate, coolDownText)
                .setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(transferRateComponent);
    }

    @Override
    public void tick() {

        if(level.isClientSide && rotateController != null) {
            // clamp so we don't divide by zero
            var divider = customCooldownTicks == 0 ? 1 : customCooldownTicks;
            rotateController.setAnimationSpeed(20.0 / divider);
        }

        if (level.isClientSide || disabled) {
            return;
        }
        if (shouldStall())
            return;

        var fromPos = getFromPos();
        var toPos = getToPos();
        if (fromPos != null && level.isLoaded(fromPos)) {
            if (!(level.getBlockEntity(fromPos) instanceof AbstractSourceMachine)) {
                setFromPos(null);
                updateBlock();
                return;
            } else if (level.getBlockEntity(fromPos) instanceof AbstractSourceMachine fromTile) {
                if (transferSource(fromTile, this) > 0) {
                    updateBlock();
                    ParticleUtil.spawnFollowProjectile(level, fromPos, worldPosition, this.getColor());
                }
            }
        }

        if (toPos != null && level.isLoaded(toPos)) {
            if (!(level.getBlockEntity(toPos) instanceof AbstractSourceMachine)) {
                setToPos(null);
                updateBlock();
                return;
            }
            AbstractSourceMachine toTile = (AbstractSourceMachine) this.level.getBlockEntity(toPos);
            if (transferSource(this, toTile) > 0) {
                ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos, this.getColor());
            }
        }

    }

    private boolean shouldStall() {
        if (this.customCooldownTicks == 0) {
            return false;
        }
        if (this.customCooldownTicks == -1) {
            return level.getGameTime() % 20 != 0;
        }
        return level.getGameTime() % customCooldownTicks != 0;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(customCooldownTicks != -1) {
            tag.putInt("CustomCooldown", customCooldownTicks);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        var coolDown = tag.getInt("CustomCooldown");
        if(coolDown != -1) {
            this.customCooldownTicks = coolDown;
        }
    }

    @Override
    public void setCooldownTicks(int ticks) {
        this.customCooldownTicks = ticks;
    }

    @Override
    public int getCooldownTicks() {
        return this.customCooldownTicks;
    }

    private AnimationController rotateController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        rotateController = new AnimationController<>(this, "rotate_controller", 0, this::idlePredicate);
        data.add(rotateController);
        data.add(new AnimationController<>(this, "float_controller", 0, this::floatPredicate));
    }

    private <P extends GeoAnimatable> PlayState idlePredicate(AnimationState<P> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("floating"));
        return PlayState.CONTINUE;
    }

    private <P extends GeoAnimatable> PlayState floatPredicate(AnimationState<P> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("rotation"));
        return PlayState.CONTINUE;
    }

}
