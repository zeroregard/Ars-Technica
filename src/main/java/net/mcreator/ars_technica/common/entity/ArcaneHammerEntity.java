package net.mcreator.ars_technica.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.common.helpers.SpellResolverHelpers;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.wrapper.RecipeWrapper;
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

public class ArcaneHammerEntity extends Entity implements GeoEntity, Colorable {
    private static float UNSCALED_TIME_TILL_OBLITERATE = 0.71f;
    private static float UNSCALED_TIME_TILL_DISCARD = 1.5f;
    private static float AMPS_SIZE_MULTIPLIER = (1/3f);
    private static float AMPS_SPEED_MULTIPLIER = -(1/20f);

    private long createdTime;

    private Entity target;
    private final Level world;
    private Entity caster;
    private SpellResolver resolver;
    private float ampScalar = 1.5f;
    private float amps = 0.0f;

    private boolean processItems = false;
    private boolean didObliterate = false;

    private Color color;
    private float yaw;
    private float size = 1.0f;
    private float alpha = 0.0f;
    private float speed = 1.0f;

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
        this.entityData.set(SPEED, speed);
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
    }

    public ArcaneHammerEntity(EntityType<ArcaneHammerEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        var pos = this.getPosition(1.0f);
        world.playSound(null, pos.x, pos.y, pos.z, ArsTechnicaModSounds.OBLITERATE_CHARGE.get(), SoundSource.BLOCKS, 0.75f, speed);
        world.playSound(null, pos.x, pos.y, pos.z, ArsTechnicaModSounds.OBLITERATE_CHARGE_LARGE.get(), SoundSource.BLOCKS, amps * (1/8f) * 0.4f, speed);
        createdTime = world.getGameTime();
    }

    @Override
    public void tick() {
        if(target != null && !didObliterate) {
            this.setPos(getTargetPosition(target));
        }
        var elapsedTime = (world.getGameTime() - createdTime) / 20.0f;
        if(elapsedTime >= UNSCALED_TIME_TILL_DISCARD * (1/speed)) {
            discard();
        }
        if(elapsedTime >= UNSCALED_TIME_TILL_OBLITERATE * (1/speed) && !didObliterate && !world.isClientSide) {
            obliterate();
        }
    }

    protected void obliterate() {
        if(target != null) {
            var damageSource = getDamageSource();
            target.hurt(damageSource, getDamage());
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
        world.playSound(null, pos.x, pos.y, pos.z, ArsTechnicaModSounds.OBLITERATE_SMASH.get(), SoundSource.BLOCKS, 0.75f, speed);
        world.playSound(null, pos.x, pos.y, pos.z, ArsTechnicaModSounds.OBLITERATE_SHOCKWAVE.get(), SoundSource.BLOCKS, amps * (1/8f) * 0.4f, 1.0f);
        didObliterate = true;
        handleItems();
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


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        AnimationController<ArcaneHammerEntity> controller = new AnimationController<>(this, "hammerController", 0, this::smashAnimationPredicate);

        controller.setCustomInstructionKeyframeHandler(event -> {
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

        controller.setAnimationSpeed(speed);

        controllerRegistrar.add(controller);

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
