package com.zeroregard.ars_technica.entity.fusion;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.zeroregard.ars_technica.entity.Colorable;
import com.zeroregard.ars_technica.entity.fusion.fluids.ArcaneFusionFluids;
import com.zeroregard.ars_technica.entity.fusion.fluids.FluidSourceProvider;
import com.zeroregard.ars_technica.helpers.FluidHelper;
import com.zeroregard.ars_technica.helpers.MixingRecipeHelpers;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import com.zeroregard.ars_technica.registry.SoundRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArcaneFusionEntity extends Entity implements GeoEntity, Colorable {

    private static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    private ArcaneFusionType fusionType;

    private final ArcaneFusionParticles particleHandler;
    private final ArcaneFusionFluids fluidHandler;

    private long createdTime;
    private float elapsedTime;
    private int tickCount = 0;
    private boolean impacted = false;
    private boolean swung = false;
    private boolean failed = false;
    private static final EntityDataAccessor<Boolean> FAILED = SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> INGREDIENT_A =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> INGREDIENT_B =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> INGREDIENT_C =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> INGREDIENT_D =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);

    private double aoe = 1.0;
    private Entity caster;

    private final Level world;
    private ItemEntity resultEntity;
    private List<FluidStack> resultLiquids;

    public static float CHARGE_TIME = 1.65f;
    public static float SWING_TIME = 0.35f;
    public static float IMPACT_TIME = CHARGE_TIME + SWING_TIME;
    public static float REMOVE_TIME = IMPACT_TIME + 1.0f;

    private AnimationController<ArcaneFusionEntity> animationController;

    public SynchedEntityData getEntityData() {
        return this.entityData;
    }

    public boolean getImpacted() {
        return impacted;
    }

    public boolean getSwung() {
        return swung;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public RandomSource getRandom() {
        return random;
    }

    public int getTickCount() {
        return tickCount;
    }

    public ArcaneFusionType getFusionType() {
        return fusionType;
    }

    public ArcaneFusionEntity(@Nullable() Entity target, Vec3 position, Level world, Entity caster, Color color, SpellResolver resolver, SpellStats spellStats, String fusionTypeId) {
        super(EntityRegistry.ARCANE_FUSION_ENTITY.get(), world);
        this.world = world;
        this.aoe = 1.0 + spellStats.getAoeMultiplier();
        this.caster = caster;
        setPos(position.x, position.y, position.z);
        this.createdTime = world.getGameTime();
        this.particleHandler = new ArcaneFusionParticles(this, world);
        this.fluidHandler = new ArcaneFusionFluids(this, world);
        this.entityData.set(TYPE, fusionTypeId);
        this.fusionType = AllArcaneFusionTypes.getTypeFromId(fusionTypeId);
    }

    public ArcaneFusionEntity(EntityType<ArcaneFusionEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
        this.particleHandler = new ArcaneFusionParticles(this, world);
        this.fluidHandler = new ArcaneFusionFluids(this, world);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        createdTime = world.getGameTime();
        handleIngredients();
    }

    @Override
    public void tick() {
        elapsedTime = (world.getGameTime() - createdTime) / 20.0f;
        tickCount++;

        if(elapsedTime > REMOVE_TIME) {
            discard();
        }

        if (world.isClientSide && fusionType != null) {
            particleHandler.handleParticles();
        }

        if (failed) {
            return;
        }

        if(elapsedTime > CHARGE_TIME && !swung) {
            playWorldSound(SoundRegistry.FUSE_SWING.get(), 0.75f, 1.0f);
            swung = true;
        }

        if(elapsedTime > IMPACT_TIME && !impacted) {
            playWorldSound(SoundRegistry.FUSE_IMPACT.get(), 0.75f, 1.0f);
            outputResults();
            impacted = true;
        }

    }


    private void playWorldSound(SoundEvent soundEvent, float volume, float pitch) {
        var pos = this.getPosition(1.0f);
        world.playSound(null, pos.x, pos.y, pos.z, soundEvent, SoundSource.BLOCKS, volume, pitch);
    }

    protected void outputResults() {
        if(resultEntity != null) {
            world.addFreshEntity(resultEntity);
        }
        if(resultLiquids != null) {
            for (FluidStack fluidStack : resultLiquids) {
                FluidHelper.dumpFluid(fluidStack, world, getPosition(1.0f), 4);
            }
        }
    }

    protected void handleIngredients() {
        if(world.isClientSide || fusionType == null) {
            return;
        }
        var fluids = fluidHandler.pickupFluids();
        var itemEntities = world.getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(this.aoe));
        if (itemEntities.isEmpty() && fluids.isEmpty()) {
            onFailure("no nearby items/fluids were found");
            return;
        }

        tryCombineIngredients(itemEntities, fluids);
    }

    protected void tryCombineIngredients(List<ItemEntity> itemEntities, List<FluidSourceProvider> fluids) {
        var result = MixingRecipeHelpers.getMixingRecipe(itemEntities, fluids, world, fusionType);

        if(result.isPresent()) {
            var resultObj = result.get();
            var recipe = resultObj.recipe;
            var ingredients = resultObj.usedEntities;
            var fluidIngredients = resultObj.usedFluids;

            int maxItemIterations = Integer.MAX_VALUE;

            if(!ingredients.isEmpty()) {
                Map<ItemEntity, Integer> usageMap = new HashMap<>();

                for (var ingredient : ingredients) {
                    usageMap.put(ingredient, usageMap.getOrDefault(ingredient, 0) + 1);
                }

                for (var entry : usageMap.entrySet()) {
                    ItemEntity itemEntity = entry.getKey();
                    int usedCount = entry.getValue();
                    int totalAvailable = itemEntity.getItem().getCount();

                    int entityMaxIterations = totalAvailable / usedCount;

                    maxItemIterations = Math.min(maxItemIterations, entityMaxIterations);
                }
            }

            int maxFluidIterations = recipe.getFluidIngredients().isEmpty() ? Integer.MAX_VALUE :
                    fluidIngredients.stream()
                            .mapToInt(fluidSource -> {
                                var requiredFluid = recipe.getFluidIngredients().stream()
                                        .flatMap(ingredient -> ingredient.getMatchingFluidStacks().stream())
                                        .filter(matchingFluid -> matchingFluid.getFluid().equals(fluidSource.getFluidStack().getFluid()))
                                        .findFirst()
                                        .orElse(null);
                                return (int) Math.floor(fluidSource.getMbAmount() / requiredFluid.getAmount());
                            })
                            .min()
                            .orElse(0);
            int recipeIterations = Math.min(maxItemIterations, maxFluidIterations);
            int clampedRecipeIterations = Math.min(recipeIterations, (int)Math.round(this.aoe * 4));

            if (clampedRecipeIterations <= 0) {
                onFailure("there were not enough resources for a found recipe");
                return;
            }

            if(ingredients != null && !ingredients.isEmpty()) {
                particleHandler.setIngredientsForParticles(ingredients);
            }

            // Remove the used items based on count
            ingredients.forEach(itemEntity -> {
                var item = itemEntity.getItem();
                item.setCount(item.getCount() - clampedRecipeIterations);
                if (item.getCount() <= 0) {
                    itemEntity.discard();
                }
            });

            // Remove the used fluids
            fluidIngredients.forEach(fluidSource -> {
                // Find the matching fluid ingredient for this fluid source
                var requiredFluid = recipe.getFluidIngredients().stream()
                        .flatMap(ingredient -> ingredient.getMatchingFluidStacks().stream())
                        .filter(matchingFluid -> matchingFluid.getFluid().equals(fluidSource.getFluidStack().getFluid()))
                        .findFirst()
                        .orElse(null);

                if (requiredFluid != null) {
                    int mbToDrain = requiredFluid.getAmount() * clampedRecipeIterations;
                    fluidSource.drainFluid(mbToDrain, world);
                }
            });
            setItemResult(recipe, clampedRecipeIterations);
            setFluidResult(recipe, clampedRecipeIterations);

            playWorldSound(SoundRegistry.FUSE_CHARGE.get(), 0.75f, 1.0f);
        }
        else {
            onFailure("no recipes were found for nearby items/fluids");
        }
    }

    private void onFailure(String failureReason) {
        failed = true;
        entityData.set(FAILED, true);
        playWorldSound(SoundRegistry.FUSE_FAILED.get(), 0.75f, 1.0f);
    }

    private void setItemResult(MixingRecipe recipe, int recipeIterations) {
        RegistryAccess registryAccess = world.registryAccess();
        ItemStack recipeItemResult = recipe.getResultItem(registryAccess);
        var itemOutput = recipeItemResult.copy();
        itemOutput.setCount(itemOutput.getCount() * recipeIterations);
        resultEntity = new ItemEntity(world, getX(), getY(), getZ(), itemOutput);
    }

    private void setFluidResult(MixingRecipe recipe, int recipeIterations) {
        List<FluidStack> fluidResults = recipe.getFluidResults();
        resultLiquids = fluidResults.stream()
                .map(fluidStack -> {
                    int newAmount = fluidStack.getAmount() * recipeIterations;
                    return new FluidStack(fluidStack.getFluid(), newAmount);
                })
                .collect(Collectors.toList());
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        animationController = new AnimationController<>(this, "fusionController", 0, this::fuseAnimationPredicate);
        controllerRegistrar.add(animationController);
    }

    private PlayState fuseAnimationPredicate(AnimationState<?> event) {
        if(failed) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("fail"));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("charge"));
        }
        return PlayState.CONTINUE;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder dataBuilder) {
        dataBuilder.define(TYPE, "");
        dataBuilder.define(FAILED, false);
        dataBuilder.define(INGREDIENT_A, "");
        dataBuilder.define(INGREDIENT_B, "");
        dataBuilder.define(INGREDIENT_C, "");
        dataBuilder.define(INGREDIENT_D, "");
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        particleHandler.onSyncedDataUpdated(key);
        if(TYPE.equals(key)) {
            String typeId = this.entityData.get(TYPE);
            this.fusionType = AllArcaneFusionTypes.getTypeFromId(typeId);
        }
        if(FAILED.equals(key)) {
            this.failed = this.entityData.get(FAILED);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public double getAlpha() {
        return 1;
    }
}
