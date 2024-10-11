package net.mcreator.ars_technica.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.common.helpers.SpellResolverHelpers;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArcaneHammerEntity extends Entity implements GeoEntity {
    private Color color;
    private int ticks = 0;
    private static int TICKS_TILL_OBLITERATION = 14;
    private static int TICKS_TILL_DISCARD = 30;
    private Entity target;
    private final Level world;
    private Entity caster;
    private boolean didObliterate = false;
    private boolean processItems = false;
    private SpellResolver resolver;
    private float yaw;

    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(ArcaneProcessEntity.class, EntityDataSerializers.FLOAT);

    private static Vec3 getTargetPosition(Entity target) {
        return target.getPosition(1.0f).add(0, 0.5, 0);
    }

    public void setYaw(float yaw) {
        this.entityData.set(YAW, yaw);
    }

    public float getYaw() {
        return this.yaw;
    }

    public double getAlpha() {
        if (ticks == 0) return 0.0;
        if (ticks >= 1 && ticks <= 9) return (ticks - 1) * (1/9.0);
        if (ticks >= 10 && ticks < 14) return 1.0;
        if (ticks >= 14 && ticks <= 30) {
            return 1.0 - (ticks - 14) / 16.0;
        }
        return 0.0;
    }

    public Color getColor() {
        return color;
    }


    protected void setColor(Color color) {
        this.entityData.set(COLOR, color.getColor());
    }

    public ArcaneHammerEntity(@Nullable() Entity target, Vec3 position, Level world, Entity caster, Color color, SpellResolver resolver, boolean processItems) {
        super(EntityRegistry.ARCANE_HAMMER_ENTITY.get(), world);
        this.world = world;
        this.setPos(position.x, position.y, position.z);
        this.caster = caster;
        this.color = color;
        this.processItems = processItems;
        this.resolver = resolver;
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
        if (!world.isClientSide) {
            handleItems();
        }
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
        this.entityData.define(YAW, 0f);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (COLOR.equals(key)) {
            this.color = new Color(this.entityData.get(COLOR));
        }

        if(YAW.equals(key)) {
            this.yaw = this.entityData.get(YAW);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
