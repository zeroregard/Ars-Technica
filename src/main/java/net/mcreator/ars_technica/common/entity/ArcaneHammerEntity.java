package net.mcreator.ars_technica.common.entity;

import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class ArcaneHammerEntity extends Entity implements GeoEntity {
    private Color color;
    private int ticks = 0;
    private static int TICKS_TILL_OBLITERATION = 14;
    private static int TICKS_TILL_DISCARD = 30;
    private Entity target;
    private final Level world;
    private Entity caster;
    private boolean didObliterate = false;

    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.INT);

    private static Vec3 getTargetPosition(Entity target) {
        return target.getPosition(1.0f).add(0, 0.5, 0);
    }

    public double getAlpha() {
        if (ticks < 14) {
            return 1.0;
        } else if (ticks <= 30) {
            return 1.0 - (ticks - 14) / 16.0;
        } else {
            return 0.0;
        }
    }

    public Color getColor() {
        return color;
    }


    protected void setColor(Color color) {
        this.entityData.set(COLOR, color.getColor());
    }

    public ArcaneHammerEntity(Entity target, Level world, Entity caster, Color color) {
        this(getTargetPosition(target), world, caster, color);
        this.target = target;
        this.color = color;
        setColor(color);
    }

    public ArcaneHammerEntity(Vec3 position, Level world, Entity caster, Color color) {
        super(EntityRegistry.ARCANE_HAMMER_ENTITY.get(), world);
        this.world = world;
        this.setPos(position.x, position.y, position.z);
        this.caster = caster;
        this.color = color;
        setColor(color);
    }

    public ArcaneHammerEntity(EntityType<ArcaneHammerEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        var pos = this.getPosition(1.0f);
        world.playSound(null, pos.x, pos.y, pos.z, ArsTechnicaModSounds.OBLITERATE_CHARGE.get(), SoundSource.BLOCKS, 0.75f, 1.0f);
    }

    @Override
    public void tick() {
        if(target != null && !didObliterate) {
            this.setPos(getTargetPosition(target));
        }
        ticks++;
        if(ticks >= TICKS_TILL_DISCARD) {
            discard();
        }

        if(ticks == TICKS_TILL_OBLITERATION) {
            obliterate();
        }
    }

    protected void obliterate() {
        if(target != null) {
            var damageSource = getDamageSource();
            target.hurt(damageSource, 5);
        }
        var pos = getPosition(1.0f);
        world.playSound(null, pos.x, pos.y, pos.z, ArsTechnicaModSounds.OBLITERATE_SMASH.get(), SoundSource.BLOCKS, 0.75f, 1.0f);
        didObliterate = true;
    }

    protected DamageSource getDamageSource() {
        Holder<DamageType> magicDamageType = world.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.INDIRECT_MAGIC);
        DamageSource hammerDamageSource = new DamageSource(magicDamageType, this, caster);
        return hammerDamageSource;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "hammerController", 0, this::smashAnimationPredicate));
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    private PlayState smashAnimationPredicate(AnimationState<?> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("smash"));
        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (COLOR.equals(key)) {
            this.color = new Color(this.entityData.get(COLOR));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
