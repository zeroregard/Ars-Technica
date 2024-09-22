package net.mcreator.ars_technica.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.client.events.ClientHandler;
import net.mcreator.ars_technica.common.helpers.SpellResolverHelpers;
import net.mcreator.ars_technica.common.kinetics.WhirlCurrent;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class WhirlEntity extends Entity implements IAirCurrentSource, GeoEntity {

    private float radius;
    private int duration;
    private final Level world;
    private float speed = 0.05f;
    private FanProcessingType processor;
    private WhirlCurrent current;
    private final SpellResolver spellResolver;
    private boolean soundPlaying;

    private static final EntityDataAccessor<String> PROCESSOR_TYPE = SynchedEntityData.defineId(WhirlEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(WhirlEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(WhirlEntity.class, EntityDataSerializers.FLOAT);



    public float getRadius() {
        return radius;
    }

    public Level getLevel() {
        return world;
    }

    public FanProcessingType getProcessor() {
        return processor;
    }

    public WhirlEntity(EntityType<? extends WhirlEntity> entityType, Level world) {
        super(entityType, world);
        this.radius = 1.0f;
        this.duration = 100;
        this.world = world;
        this.spellResolver = null;
    }

    public WhirlEntity(Level world, Vec3 position, float radius, int duration, FanProcessingType processor, SpellResolver spellResolver) {
        super(EntityRegistry.WHIRL_ENTITY.get(), world);
        this.setPos(position.x, position.y, position.z);
        this.duration = duration;
        this.world = world;
        this.spellResolver = spellResolver;
        setRadius(radius);
        this.radius = radius; // WhirlCurrent needs this immediately, cannot wait for entityData update
        setSpeed(SpellResolverHelpers.hasTransmutationFocus(spellResolver) ? 0.1f : 0.05f);
        setProcessor(processor);
        this.current = new WhirlCurrent(this);
    }


    @Override
    public void tick() {
        super.tick();
        handleWhirlwindEffect();
        // handleGravity();
    }

    private void handleGravity() {
        BlockPos belowPos = getBlockPosBelowThatAffectsMyMovement();
        boolean onGround = !this.world.getBlockState(belowPos).isAir();
        if (!onGround) {
            moveDown();
        }
    }

    private void moveDown() {
        this.setPos(this.getX(), this.getY() - 0.03f, this.getZ());
    }

    private void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    private void setProcessor(FanProcessingType processor) {
        this.entityData.set(PROCESSOR_TYPE, getProcessorLegacyId(processor));
    }

    private void setRadius(float radius) {
        this.entityData.set(RADIUS, radius);
    }

    private String getProcessorLegacyId(FanProcessingType processor) {
        if (processor == AllFanProcessingTypes.BLASTING) {
            return "BLASTING";
        }
        if (processor == AllFanProcessingTypes.HAUNTING) {
            return "HAUNTING";
        }
        if (processor == AllFanProcessingTypes.SMOKING) {
            return "SMOKING";
        }
        if (processor == AllFanProcessingTypes.SPLASHING) {
            return "SPLASHING";
        }
        return "NONE";
    }

    private void handleWhirlwindEffect() {
        if (!this.world.isClientSide) {
            duration--;
            if (current != null) {
                current.tick(this.spellResolver);
            }
            if (duration <= 0) {
                if (current != null) {
                    current.stopAffectedItems();
                }
                this.discard();
            }
        }

    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PROCESSOR_TYPE, AllFanProcessingTypes.NONE.toString());
        this.entityData.define(SPEED, 0.05f);
        this.entityData.define(RADIUS, 1.5f);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (SPEED.equals(key)) {
            this.speed = this.entityData.get(SPEED);
        }

        if (PROCESSOR_TYPE.equals(key)) {
            this.processor = AllFanProcessingTypes.parseLegacy(this.entityData.get(PROCESSOR_TYPE));
            initSound();
        }

        if (RADIUS.equals(key)) {
            this.radius = this.entityData.get(RADIUS);
        }
    }

    private void initSound() {
        if (world.isClientSide && !soundPlaying) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handleWhirlSound(this, processor, speed));
            soundPlaying = true;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.duration = compound.getInt("Duration");
        setRadius(compound.getFloat("Radius"));
        setSpeed(compound.getFloat("Speed"));

        if (compound.contains("ProcessorType")) {
            String processorType = compound.getString("ProcessorType");
            setProcessor(AllFanProcessingTypes.parseLegacy(processorType));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Duration", this.duration);
        compound.putFloat("Radius", this.radius);
        compound.putFloat("Speed", this.speed);
        if (this.processor != null) {
            compound.putString("ProcessorType", getProcessorLegacyId(this.processor));
        }
    }

    @Override
    public @Nullable AirCurrent getAirCurrent() {
        return null;
    }

    @Override
    public @Nullable Level getAirCurrentWorld() {
        return null;
    }

    @Override
    public BlockPos getAirCurrentPos() {
        return null;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public Direction getAirflowOriginSide() {
        return null;
    }

    @Override
    public @Nullable Direction getAirFlowDirection() {
        return null;
    }

    @Override
    public boolean isSourceRemoved() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "rotateController", 0, this::rotateAnimationPredicate));
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    private PlayState rotateAnimationPredicate(AnimationState<?> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("rotation"));
        event.getController().setAnimationSpeed(0.75f * (speed / 0.05f));
        return PlayState.CONTINUE;
    }
}