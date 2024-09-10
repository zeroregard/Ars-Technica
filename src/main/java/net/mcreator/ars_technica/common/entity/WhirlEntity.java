package net.mcreator.ars_technica.common.entity;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.kinetics.WhirlCurrent;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


public class WhirlEntity extends Entity implements IAirCurrentSource {

    private double radius;
    private int duration;
    private final Level world;
    private float speed = 0.2f;
    private FanProcessingType processor;
    private final WhirlCurrent current;

    public double getRadius() {
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
        this.radius = 1.0;
        this.duration = 100;
        this.world = world;
        this.processor = AllFanProcessingTypes.NONE;

        this.current =  new WhirlCurrent(this);
    }

    public WhirlEntity(Level world, Vec3 position, double radius, int duration, FanProcessingType processor) {
        super(EntityRegistry.WHIRL_ENTITY.get(), world);
        this.setPos(position.x, position.y, position.z);
        this.radius = radius;
        this.duration = duration;
        this.world = world;
        this.processor = processor;

        this.current =  new WhirlCurrent(this);
    }

    @Override
    public void tick() {
        super.tick();
        handleWhirlwindEffect();
    }

    private void handleWhirlwindEffect() {
        if (!this.world.isClientSide) {
            duration--;
            ArsTechnicaMod.LOGGER.info(duration);
        }
        if (duration <= 0) {
            current.stopAffectedItems();
            this.discard();
            return;
        }

        current.tick();
    }

    @Override
    protected void defineSynchedData() {
        // Define data watchers if needed
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.duration = compound.getInt("Duration");
        this.radius = compound.getDouble("Radius");
        this.speed = compound.getFloat("Speed");

        if (compound.contains("ProcessorType")) {
            String processorType = compound.getString("ProcessorType");
            this.processor = FanProcessingType.parse(processorType);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Duration", this.duration);
        compound.putDouble("Radius", this.radius);
        compound.putDouble("Speed", this.speed);
        if (this.processor != null) {
            compound.putString("ProcessorType", this.processor.toString());
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
}