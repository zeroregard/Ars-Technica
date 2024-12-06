package net.mcreator.ars_technica.common.entity.fusion;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.particles.SpiralDustParticleTypeData;
import net.mcreator.ars_technica.common.entity.Colorable;
import net.mcreator.ars_technica.common.entity.fusion.fluids.ArcaneFusionFluids;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArcaneFusionEntity extends Entity implements GeoEntity, Colorable {

    private final ArcaneFusionParticles particleHandler;
    private final ArcaneFusionFluids fluidHandler;

    private long createdTime;
    private float elapsedTime;
    private int tickCount = 0;
    private boolean impacted = false;
    private boolean swung = false;

    private double aoe = 1.0;
    private Entity caster;

    private final Level world;
    private ItemEntity resultEntity;

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

    public ArcaneFusionEntity(@Nullable() Entity target, Vec3 position, Level world, Entity caster, Color color, SpellResolver resolver, SpellStats spellStats) {
        super(EntityRegistry.ARCANE_FUSION_ENTITY.get(), world);
        this.world = world;
        this.aoe = 1.0 + spellStats.getAoeMultiplier();
        this.caster = caster;
        setPos(position.x, position.y, position.z);
        this.createdTime = world.getGameTime();
        this.particleHandler = new ArcaneFusionParticles(this, world);
        this.fluidHandler = new ArcaneFusionFluids(this, world);
    }

    public ArcaneFusionEntity(EntityType<ArcaneFusionEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
        this.particleHandler = new ArcaneFusionParticles(this, world);
        this.fluidHandler = new ArcaneFusionFluids(this, world);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        createdTime = world.getGameTime();
        handleIngredients();
    }

    @Override
    public void tick() {
        elapsedTime = (world.getGameTime() - createdTime) / 20.0f;

        if(elapsedTime > CHARGE_TIME && !swung) {
            playWorldSound(ArsTechnicaModSounds.FUSE_SWING.get(), 0.75f, 1.0f);
            swung = true;
        }

        if(elapsedTime > IMPACT_TIME && !impacted) {
            playWorldSound(ArsTechnicaModSounds.FUSE_IMPACT.get(), 0.75f, 1.0f);
            if(resultEntity != null) {
                world.addFreshEntity(resultEntity);
            }
            impacted = true;
        }

        if(elapsedTime > REMOVE_TIME) {
            discard();
        }

        if (world.isClientSide) {
            particleHandler.handleParticles();
        }

        tickCount++;
    }


    private void playWorldSound(SoundEvent soundEvent, float volume, float pitch) {
        var pos = this.getPosition(1.0f);
        world.playSound(null, pos.x, pos.y, pos.z, soundEvent, SoundSource.BLOCKS, volume, pitch);
    }


    protected void handleIngredients() {
        if(world.isClientSide) {
            return;
        }
        // TODO: actually use the fluids
        var fluids = fluidHandler.pickupFluids();
        for (int i = 0; i < fluids.size(); i++) {
            var fluid = fluids.get(i);
            var amount = fluid.getFluidStack().getAmount();
            var type = fluid.getFluidStack().getFluid().getFluidType();
            ArsTechnicaMod.LOGGER.info(type + ", " + amount);
        }
        ArsTechnicaMod.LOGGER.info(fluids);
        handleWorldItems();
    }

    protected void handleWorldItems() {

        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(aoe));
        if (itemEntities.isEmpty()) {
            return;
        }
        var result = RecipeHelpers.getFusionRecipeForItems(itemEntities, world);

        if(result.isPresent()) {
            var resultObj = result.get();
            var recipe = resultObj.getFirst();
            var ingredients = resultObj.getSecond();

            // Calculate how many times we can execute the recipe by finding which ingredient is the least
            // This assumes that all recipes have a 1:1 relationship between ingredients though!
            int recipeIterations = ingredients.stream()
                    .collect(Collectors.groupingBy(itemEntity -> itemEntity.getItem().getItem(),
                            Collectors.summingInt(item -> item.getItem().getCount())))
                    .values()
                    .stream()
                    .min(Integer::compare)
                    .orElse(0);

            if(ingredients != null && !ingredients.isEmpty()) {
                particleHandler.setIngredientsForParticles(ingredients);
            }

            // Remove the used items based on count
            ingredients.forEach(itemEntity -> {
                var item = itemEntity.getItem();
                item.setCount(item.getCount() - recipeIterations);
                if (item.getCount() <= 0) {
                    itemEntity.discard();
                }
            });


            // Create the output item - for now do nothing with fluids, later on try to place in nearest storage tank or basin
            RegistryAccess registryAccess = world.registryAccess();
            ItemStack recipeItemResult = recipe.getResultItem(registryAccess);
            var itemOutput = recipeItemResult.copy();
            itemOutput.setCount(itemOutput.getCount() * recipeIterations);
            // var fluidOutput = recipe.getFluidResults();

            playWorldSound(ArsTechnicaModSounds.FUSE_CHARGE.get(), 0.75f, 1.0f);

            resultEntity = new ItemEntity(world, getX(), getY(), getZ(), itemOutput);
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        animationController = new AnimationController<>(this, "fusionController", 0, this::fuseAnimationPredicate);
        controllerRegistrar.add(animationController);
    }

    private PlayState fuseAnimationPredicate(AnimationState<?> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("charge"));
        return PlayState.CONTINUE;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    @Override
    protected void defineSynchedData() {
        //
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        particleHandler.onSyncedDataUpdated(key);
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
