package net.mcreator.ars_technica.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.foundation.utility.VecHelper;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.particles.SpiralDustParticleTypeData;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.Direction;
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
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArcaneFusionEntity extends Entity implements GeoEntity, Colorable  {

    private double aoe = 1.0;
    private int tickCount = 0;
    private Entity caster;
    private long createdTime;
    private float elapsedTime;
    private final Level world;
    private List<ItemStack> recipeItemStacks;
    private ItemEntity resultEntity;

    // Preferably this should be just 1 list but yeah
    private static final EntityDataAccessor<String> INGREDIENT_A =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> INGREDIENT_B =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> INGREDIENT_C =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> INGREDIENT_D =
            SynchedEntityData.defineId(ArcaneFusionEntity.class, EntityDataSerializers.STRING);
    private ItemStack ingredientA;
    private ItemStack ingredientB;
    private ItemStack ingredientC;
    private ItemStack ingredientD;

    private static float TIME_TO_ANGLE_MULTIPLIER = 1.5f;
    private static float CHARGE_TIME = 1.65f;
    private static float SWING_TIME = 0.35f;
    private static float IMPACT_TIME = CHARGE_TIME + SWING_TIME;
    private static float REMOVE_TIME = IMPACT_TIME + 1.0f;

    private static ParticleColor particleColorA = new ParticleColor(210, 0, 255);
    private static ParticleColor particleColorB = new ParticleColor(246, 25, 151);
    private static ParticleColor particleColorC = new ParticleColor(255, 0, 99);
    private static ParticleColor particleColorD = new ParticleColor(255, 118, 0);
    private static List<ParticleColor> particleColors = new ArrayList<>(Arrays.asList(particleColorA, particleColorB, particleColorC, particleColorD));

    private boolean impacted = false;
    private boolean swung = false;
    private boolean rendered_impact_particles = false;

    private AnimationController<ArcaneFusionEntity> animationController;

    public ArcaneFusionEntity(@Nullable() Entity target, Vec3 position, Level world, Entity caster, Color color, SpellResolver resolver, SpellStats spellStats) {
        super(EntityRegistry.ARCANE_FUSION_ENTITY.get(), world);
        this.world = world;
        this.aoe = 1.0 + spellStats.getAoeMultiplier();
        this.caster = caster;
        setPos(position.x, position.y, position.z);
        this.createdTime = world.getGameTime();
    }

    public ArcaneFusionEntity(EntityType<ArcaneFusionEntity> entityType, Level world) {
        super(entityType, world);
        this.world = world;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        createdTime = world.getGameTime();
        handleItems();
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
            handleClientParticles();
        }

        tickCount++;
    }

    private void handleClientParticles() {
        setClientSideIngredientList();
        if (world == null || recipeItemStacks == null) {
            return;
        }
        if(!impacted) {
            var diameterMultiplier = swung ? 1.0f - (elapsedTime - CHARGE_TIME) * 2 : 1.0f;
            renderCircleParticles(diameterMultiplier);
            renderFusionParticles();
        }
        if(impacted && !rendered_impact_particles) {
            renderImpactParticles();
            rendered_impact_particles = true;
        }
    }

    private void renderCircleParticles(float diameterMultiplier) {
        int numStacks = Math.min(recipeItemStacks.size(), 4);
        double angleStep = 360.0 / numStacks;
        Vec3 center = getPosition(1.0f);

        double speedMultiplier = TIME_TO_ANGLE_MULTIPLIER * Math.exp(elapsedTime / 1.0);
        double circleDiameter = 0.3f * Math.exp(elapsedTime) * diameterMultiplier;
        double circleRadius = circleDiameter / 2.0;

        for (int i = 0; i < numStacks; i++) {
            double angle = Math.toRadians(i * angleStep) + elapsedTime * speedMultiplier;
            double offsetX = circleRadius * Math.cos(angle) + random.nextGaussian() * 0.1;
            double offsetZ = circleRadius * Math.sin(angle) + random.nextGaussian() * 0.1;

            Vec3 particlePos = center.add(offsetX, 0.25f, offsetZ);
            ItemStack itemStack = recipeItemStacks.get(i);
            ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
            world.addParticle(data, particlePos.x, particlePos.y, particlePos.z, 0, 0.1, 0);
        }
    }

    private void renderFusionParticles() {
        if(tickCount % 2 == 0) {
            var particleColorIndex = random.nextIntBetweenInclusive(0, 3);
            var particleColor = particleColors.get(particleColorIndex);
            var particleData = new SpiralDustParticleTypeData(particleColor, false, 1.0f, 1.0f, 20);
            addParticle(particleData, Math.toRadians(random.nextGaussian() * 360), 0, 0.04, 0);
        }
    }

    private void renderImpactParticles() {
        addParticle(ParticleTypes.EXPLOSION, 0, 0, 0, 0);
        for (int i = 0; i < 5; i++) {
            addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, 0, 0.0002, 0.4, 0.05);
        }

        var itemParticleCount = 16;
        var speedMultiplier = 0.5;
        double angleStep = 360.0 / itemParticleCount;
        for (int i = 0; i < 16; i++) {
            double angle = Math.toRadians(i * angleStep);
            ItemStack itemStack = recipeItemStacks.get(i % recipeItemStacks.size());
            ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
            addParticle(data, angle, 0.1, 0.2, 0.2);
        }
    }

    private void addParticle(ParticleOptions particleData, double angle, double speedMultiplier, double offsetMultiplier, double ySpeed) {
        double offsetX = random.nextGaussian() * offsetMultiplier;
        double offsetY = random.nextGaussian() * offsetMultiplier;
        double offsetZ = random.nextGaussian() * offsetMultiplier;
        double speedX =  Math.cos(angle) + random.nextGaussian() * speedMultiplier;
        double speedZ =  Math.sin(angle) + random.nextGaussian() * speedMultiplier;
        world.addParticle(particleData, getX() + offsetX, getY() + offsetY, getZ() + offsetZ, offsetX, ySpeed, offsetZ);
    }

    private void playWorldSound(SoundEvent soundEvent, float volume, float pitch) {
        var pos = this.getPosition(1.0f);
        world.playSound(null, pos.x, pos.y, pos.z, soundEvent, SoundSource.BLOCKS, volume, pitch);
    }


    protected void handleItems() {
        if(world.isClientSide) {
            return;
        }
        // TODO: try to get the nearest basin in AOE area and get ingredients from that or fall back to item entities in world
        handleWorldItems();
    }

    protected void handleWorldItems() {

        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(aoe));
        if (itemEntities.isEmpty()) {
            return;
        }
        var filteredItemEntities = itemEntities.stream()
                .collect(Collectors.groupingBy(itemEntity -> itemEntity.getItem().getItem()))
                .entrySet().stream()
                .limit(9) // Consider maximum 9 different types of items (default behaviour of a basin)
                .collect(Collectors.toMap(Map.Entry::getKey, InjectorGroupInfo.Map.Entry::getValue));
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
                setIngredientsForParticles(ingredients);
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

    private void setIngredientsForParticles(List<ItemEntity> ingredients) {
        if (ingredients.size() > 0) {
            this.entityData.set(INGREDIENT_A, getItemRegistryName(ingredients.get(0).getItem().getItem()));
        }
        if (ingredients.size() > 1) {
            this.entityData.set(INGREDIENT_B, getItemRegistryName(ingredients.get(1).getItem().getItem()));
        }
        if (ingredients.size() > 2) {
            this.entityData.set(INGREDIENT_C, getItemRegistryName(ingredients.get(2).getItem().getItem()));
        }
        if (ingredients.size() > 3) {
            this.entityData.set(INGREDIENT_D, getItemRegistryName(ingredients.get(3).getItem().getItem()));
        }
    }

    private String getItemRegistryName(Item item) {
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        return registryName != null ? registryName.toString() : "";
    }

    private void setClientSideIngredientList() {
        if (recipeItemStacks == null && ingredientA != null) {
            var temporaryList = new ArrayList<>(Arrays.asList(ingredientA));
            addNonNullToList(ingredientB, temporaryList);
            addNonNullToList(ingredientC, temporaryList);
            addNonNullToList(ingredientD, temporaryList);
            recipeItemStacks = temporaryList.stream().toList();
        }
    }

    private void addNonNullToList(@Nullable ItemStack itemStack, ArrayList<ItemStack> list) {
        if (itemStack != null) {
            list.add(itemStack);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(INGREDIENT_A, "");
        this.entityData.define(INGREDIENT_B, "");
        this.entityData.define(INGREDIENT_C, "");
        this.entityData.define(INGREDIENT_D, "");
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (INGREDIENT_A.equals(key)) {
            this.ingredientA = getItemStackFromRegistry(this.entityData.get(INGREDIENT_A));
        }
        if (INGREDIENT_B.equals(key)) {
            this.ingredientB = getItemStackFromRegistry(this.entityData.get(INGREDIENT_B));
        }
        if (INGREDIENT_C.equals(key)) {
            this.ingredientC = getItemStackFromRegistry(this.entityData.get(INGREDIENT_C));
        }
        if (INGREDIENT_D.equals(key)) {
            this.ingredientD = getItemStackFromRegistry(this.entityData.get(INGREDIENT_D));
        }
    }

    private ItemStack getItemStackFromRegistry(String registryName) {
        if (registryName.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ResourceLocation resourceLocation = new ResourceLocation(registryName);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
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
