package net.mcreator.ars_technica.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.events.ModParticles;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.common.helpers.SpellResolverHelpers;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.network.ParticleEffectPacket;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.mcreator.ars_technica.setup.NetworkHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.keyframe.event.data.CustomInstructionKeyframeData;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.mcreator.ars_technica.common.helpers.PlayerHelpers.getNearbyPlayers;

public class ArcaneHammerEntity extends Entity implements GeoEntity, Colorable {
    private static float UNSCALED_CHARGE_TIME = 2.0f;
    private static float UNSCALED_TIME_TILL_OBLITERATE = 0.25f;
    private static float UNSCALED_TIME_TILL_DISCARD = 1.0f;
    private static float AMPS_SIZE_MULTIPLIER = (1/3f);
    private static float AMPS_SPEED_MULTIPLIER = -(1/20f);

    private long createdTime;
    private long chargedTime;

    private Entity target;
    private final Level world;
    private Entity caster;
    private SpellResolver resolver;
    private float ampScalar = 1.5f;
    private float amps = 0.0f;

    private boolean processItems = false;
    private boolean didObliterate = false;
    private boolean isCharging = true;
    private boolean chargeAnimationPlayed = false;

    private Color color;
    private float yaw;
    private float size = 1.0f;
    private float alpha = 0.0f;
    private float speed = 1.0f;
    private AnimationController<ArcaneHammerEntity> animationController;

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ALPHA = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.FLOAT);

    private static Vec3 getTargetPosition(Entity target) {
        return target.getPosition(1.0f).add(0, 0.5, 0);
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.entityData.set(YAW, yaw);
    }

    public double getAlpha() {
        return alpha;
    }

    protected void setAlpha(float alpha) {
        this.entityData.set(ALPHA, alpha);
    }

    public Color getColor() {
        return color;
    }

    protected void setColor(Color color) {
        this.entityData.set(COLOR, color.getColor());
    }

    public float getSize() {
        return size;
    }

    protected void setSize(float size ) {
        this.entityData.set(SIZE, size);
    }

    protected void setSpeed(float speed) {
        var clamped = Math.max(0.6f, speed);
        this.speed = clamped;
        this.entityData.set(SPEED, clamped);
    }

    public ArcaneHammerEntity(@Nullable() Entity target, Vec3 position, Level world, Entity caster, Color color, SpellResolver resolver, SpellStats spellStats) {
        super(EntityRegistry.ARCANE_HAMMER_ENTITY.get(), world);
        this.world = world;
        this.caster = caster;
        this.processItems = spellStats.isSensitive();
        this.resolver = resolver;
        this.amps = (float)spellStats.getAmpMultiplier();
        this.target = target;
        setPos(position.x, position.y, position.z);
        setColor(color);
        setSize(1.0f + amps * AMPS_SIZE_MULTIPLIER);
        setSpeed(1.0f + amps * AMPS_SPEED_MULTIPLIER);
        this.createdTime = world.getGameTime();
    }

    public ArcaneHammerEntity(EntityType<ArcaneHammerEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        playWorldSound(ArsTechnicaModSounds.OBLITERATE_CHARGE.get(), 0.75f, speed);
        playWorldSound(ArsTechnicaModSounds.OBLITERATE_CHARGE_LARGE.get(), getLargeSoundVolume(), speed);
        createdTime = world.getGameTime();
    }

    private void playWorldSound(SoundEvent soundEvent, float volume, float pitch) {
        var pos = this.getPosition(1.0f);
        world.playSound(null, pos.x, pos.y, pos.z, soundEvent, SoundSource.BLOCKS, volume, pitch);
    }

    private float getChargeSpeed() {
        return speed * 3;
    }

    private float getLargeSoundVolume() {
        return Math.min(1.0f, amps * (1/8f) * 0.4f);
    }

    @Override
    public void tick() {
        if(target != null && !didObliterate) {
            this.setPos(getTargetPosition(target));
        }

        var totalElapsedTime = (world.getGameTime() - createdTime) / 20.0f;
        if(totalElapsedTime >= UNSCALED_CHARGE_TIME / getChargeSpeed() && isCharging) {
            isCharging = false;
            chargedTime = world.getGameTime();
            if (world.isClientSide) {
                setAnimationSpeed(1.0f);
            }
            playWorldSound(ArsTechnicaModSounds.OBLITERATE_SWING.get(), 1.0f, 1.0f);
        }

        if (!isCharging) {
            var elapsedPostChargeTime = (world.getGameTime() - chargedTime) / 20.0f;

            if(elapsedPostChargeTime >= UNSCALED_TIME_TILL_OBLITERATE && !didObliterate && !world.isClientSide) {
                obliterate();
            }

            if(elapsedPostChargeTime >= UNSCALED_TIME_TILL_DISCARD) {
                discard();
            }
        }

    }

    protected void obliterate() {
        if(target != null) {
            if(!processItems) {
                var damageSource = getDamageSource();
                target.hurt(damageSource, getDamage());
                if (target instanceof Witch witch) {
                    if(witch.getHealth() <= 0) {
                        if(caster != null && caster instanceof ServerPlayer serverPlayer) {
                            triggerAdvancement(serverPlayer);
                        }
                    }
                }
            }
            if (resolver != null) {
                resolver.onResolveEffect(world, new EntityHitResult(target));
            }
        } else {
            var pos = getPosition(1.0f).add(-1f, -1f, -1f);
            var blockPos = new BlockPos((int) Math.round(pos.x), (int) Math.round(pos.y),(int)Math.round(pos.z));
            if (resolver != null) {
                resolver.onResolveEffect(world, new
                        BlockHitResult(pos, Direction.UP, blockPos, false));
            }
        }
        var pos = getPosition(1.0f);
        playWorldSound(ArsTechnicaModSounds.OBLITERATE_SMASH.get(), 0.75f, 1.0f);
        playWorldSound(ArsTechnicaModSounds.OBLITERATE_SHOCKWAVE.get(), getLargeSoundVolume(), 1.0f);
        didObliterate = true;
        handleItems();
    }

    private void triggerAdvancement(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(new ResourceLocation(ArsTechnicaMod.MODID, "hammered_witch"));
        player.getAdvancements().award(advancement, "triggered_by_obliterate");
    }

    protected float getDamage() {
        var ampsDamage =  ampScalar * amps;
        var focusDamage = SpellResolverHelpers.shouldDoubleOutputs(resolver) ? ampsDamage : 0f;
        return 5 + ampsDamage + focusDamage;
    }

    protected void handleItems() {
        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(1.0));
        if(processItems) {
            processItems(itemEntities);
            sendProcessingParticles();
        }
        else {
            itemEntities.forEach(ItemEntity::discard);
        }
    }

    private void processItems(List<ItemEntity> itemEntities) {
        itemEntities.forEach(itemEntity -> {
            var itemStack = itemEntity.getItem();
            Optional<ProcessingRecipe<RecipeWrapper>> recipe = RecipeHelpers.getCrushingRecipeForItemStack(itemStack, world);
            List<ItemStack> list = new ArrayList<>();
            if(recipe.isPresent()) {
                int rolls = itemStack.getCount();
                for (int roll = 0; roll < rolls; roll++) {
                    List<ItemStack> rolledResults = recipe.get().rollResults();
                    for (int i = 0; i < rolledResults.size(); i++) {
                        ItemStack stack = rolledResults.get(i);
                        if (SpellResolverHelpers.shouldDoubleOutputs(resolver) && RecipeHelpers.isChanceBased(stack, recipe.get())) {
                            stack.grow(stack.getCount());
                        }
                        ItemHelper.addToList(stack, list);
                    }
                }
                list.forEach(result -> {
                    ItemEntity resultEntity = new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                    world.addFreshEntity(resultEntity);
                });
                itemEntity.discard();
            }
        });
    }

    protected DamageSource getDamageSource() {
        Holder<DamageType> magicDamageType = world.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.GENERIC);
        DamageSource hammerDamageSource = new DamageSource(magicDamageType, this, caster);
        return hammerDamageSource;
    }

    private void sendProcessingParticles() {
        var pos = this.position();
        List<ServerPlayer> nearbyPlayers = getNearbyPlayers(pos, world);
        ParticleType<?> particleType = ParticleTypes.DUST;

        for (ServerPlayer player : nearbyPlayers) {
            for (int i = 0; i < 10; i++) {
                var finalPos = pos.add(Math.random() / 2f, 0f, Math.random() / 2f);
                ParticleEffectPacket packet = new ParticleEffectPacket(finalPos, ModParticles.SPIRAL_DUST_TYPE.get(), ParticleColor.fromInt(color.getColor()));
                NetworkHandler.CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        animationController = new AnimationController<>(this, "hammerController", 0, this::smashAnimationPredicate);

        animationController.setCustomInstructionKeyframeHandler(event -> {
            CustomInstructionKeyframeData keyframeData = event.getKeyframeData();
            String instructions = keyframeData.getInstructions();
            if (instructions != null) {
                String[] instructionList = instructions.split(";");
                for (String instruction : instructionList) {
                    instruction = instruction.trim();

                    if (instruction.startsWith("alpha=")) {
                        String[] parts = instruction.split("=");
                        setAlpha(Float.parseFloat(parts[1]));
                    }
                }
            }
        });

        controllerRegistrar.add(animationController);

    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    private PlayState smashAnimationPredicate(AnimationState<?> event) {
        if (!isCharging) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("smash"));
            return PlayState.CONTINUE;
        }
        if(!chargeAnimationPlayed) {
            setAnimationSpeed(getChargeSpeed());
            event.getController().setAnimation(RawAnimation.begin().thenPlay("charge"));
            chargeAnimationPlayed = true;
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private void setAnimationSpeed(float speed) {
        if (animationController != null) {
            animationController.setAnimationSpeed(speed);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, 0);
        this.entityData.define(YAW, 0f);
        this.entityData.define(SIZE, 1.0f);
        this.entityData.define(ALPHA, 0.0f);
        this.entityData.define(SPEED, 1.0f);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (COLOR.equals(key))
            this.color = new Color(this.entityData.get(COLOR));
        if(YAW.equals(key))
            this.yaw = this.entityData.get(YAW);
        if(SIZE.equals(key))
            this.size = this.entityData.get(SIZE);
        if(ALPHA.equals(key))
            this.alpha = this.entityData.get(ALPHA);
        if(SPEED.equals(key))
            this.speed = this.entityData.get(SPEED);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
