package net.mcreator.ars_technica.common.entity.fusion;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.mcreator.ars_technica.client.particles.SpiralDustParticleTypeData;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArcaneFusionParticles {

    private final ArcaneFusionEntity parent;
    private final Level world;
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

    private List<ItemStack> recipeItemStacks;

    private boolean rendered_impact_particles = false;
    private static float TIME_TO_ANGLE_MULTIPLIER = 1.5f;


    public ArcaneFusionParticles(ArcaneFusionEntity parent, Level world) {
        this.parent = parent;
        this.world = world;
        this.defineSynchedData();
    }

    private void defineSynchedData() {
        parent.getEntityData().define(INGREDIENT_A, "");
        parent.getEntityData().define(INGREDIENT_B, "");
        parent.getEntityData().define(INGREDIENT_C, "");
        parent.getEntityData().define(INGREDIENT_D, "");
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (INGREDIENT_A.equals(key)) {
            this.ingredientA = getItemStackFromRegistry(parent.getEntityData().get(INGREDIENT_A));
        }
        if (INGREDIENT_B.equals(key)) {
            this.ingredientB = getItemStackFromRegistry(parent.getEntityData().get(INGREDIENT_B));
        }
        if (INGREDIENT_C.equals(key)) {
            this.ingredientC = getItemStackFromRegistry(parent.getEntityData().get(INGREDIENT_C));
        }
        if (INGREDIENT_D.equals(key)) {
            this.ingredientD = getItemStackFromRegistry(parent.getEntityData().get(INGREDIENT_D));
        }
    }

    private void setIngredientList() {
        if (recipeItemStacks == null && ingredientA != null) {
            var temporaryList = new ArrayList<>(Arrays.asList(ingredientA));
            addNonNullToList(ingredientB, temporaryList);
            addNonNullToList(ingredientC, temporaryList);
            addNonNullToList(ingredientD, temporaryList);
            recipeItemStacks = temporaryList.stream().toList();
        }
    }

    public void setIngredientsForParticles(List<ItemEntity> ingredients) {
        if (ingredients.size() > 0) {
            parent.getEntityData().set(INGREDIENT_A, getItemRegistryName(ingredients.get(0).getItem().getItem()));
        }
        if (ingredients.size() > 1) {
            parent.getEntityData().set(INGREDIENT_B, getItemRegistryName(ingredients.get(1).getItem().getItem()));
        }
        if (ingredients.size() > 2) {
            parent.getEntityData().set(INGREDIENT_C, getItemRegistryName(ingredients.get(2).getItem().getItem()));
        }
        if (ingredients.size() > 3) {
            parent.getEntityData().set(INGREDIENT_D, getItemRegistryName(ingredients.get(3).getItem().getItem()));
        }
    }

    private String getItemRegistryName(Item item) {
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        return registryName != null ? registryName.toString() : "";
    }

    private void addNonNullToList(@Nullable ItemStack itemStack, ArrayList<ItemStack> list) {
        if (itemStack != null) {
            list.add(itemStack);
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

    public void handleParticles() {
        setIngredientList();
        if (world == null || recipeItemStacks == null) {
            return;
        }
        if(!parent.getImpacted()) {
            var diameterMultiplier = parent.getSwung() ? 1.0f - (parent.getElapsedTime() - ArcaneFusionEntity.CHARGE_TIME) * 2 : 1.0f;
            renderCircleParticles(diameterMultiplier);
            renderFusionParticles();
        }
        if(parent.getImpacted() && !rendered_impact_particles) {
            renderImpactParticles();
            rendered_impact_particles = true;
        }
    }

    private void renderCircleParticles(float diameterMultiplier) {
        int numStacks = Math.min(recipeItemStacks.size(), 4);
        double angleStep = 360.0 / numStacks;
        Vec3 center = parent.getPosition(1.0f);

        float elapsedTime = parent.getElapsedTime();
        double speedMultiplier = TIME_TO_ANGLE_MULTIPLIER * Math.exp(elapsedTime / 1.0);
        double circleDiameter = 0.3f * Math.exp(elapsedTime) * diameterMultiplier;
        double circleRadius = circleDiameter / 2.0;

        for (int i = 0; i < numStacks; i++) {
            double angle = Math.toRadians(i * angleStep) + elapsedTime * speedMultiplier;
            double offsetX = circleRadius * Math.cos(angle) + parent.getRandom().nextGaussian() * 0.1;
            double offsetZ = circleRadius * Math.sin(angle) + parent.getRandom().nextGaussian() * 0.1;

            Vec3 particlePos = center.add(offsetX, 0.25f, offsetZ);
            ItemStack itemStack = recipeItemStacks.get(i);
            ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
            world.addParticle(data, particlePos.x, particlePos.y, particlePos.z, 0, 0.1, 0);
        }
    }

    private void renderFusionParticles() {
        if(parent.getTickCount() % 2 == 0) {
            var particleColorIndex = parent.getRandom().nextIntBetweenInclusive(0, 2);
            var particleColor = parent.getFusionType().getParticleColors().get(particleColorIndex);
            var particleData = new SpiralDustParticleTypeData(particleColor, false, 1.0f, 1.0f, 20);
            addParticle(particleData, Math.toRadians(parent.getRandom().nextGaussian() * 360), 0, 0.04, 0);
        }
    }

    private void renderImpactParticles() {
        addParticle(ParticleTypes.EXPLOSION, 0, 0, 0, 0);
        for (int i = 0; i < 5; i++) {
            addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, 0, 0.0002, 0.4, 0.05);
        }

        var itemParticleCount = 16;
        double angleStep = 360.0 / itemParticleCount;
        for (int i = 0; i < 16; i++) {
            double angle = Math.toRadians(i * angleStep);
            ItemStack itemStack = recipeItemStacks.get(i % recipeItemStacks.size());
            ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
            addParticle(data, angle, 0.1, 0.2, 0.2);
        }
    }

    private void addParticle(ParticleOptions particleData, double angle, double speedMultiplier, double offsetMultiplier, double ySpeed) {
        double offsetX = parent.getRandom().nextGaussian() * offsetMultiplier;
        double offsetY = parent.getRandom().nextGaussian() * offsetMultiplier;
        double offsetZ = parent.getRandom().nextGaussian() * offsetMultiplier;
        double speedX =  Math.cos(angle) + parent.getRandom().nextGaussian() * speedMultiplier;
        double speedZ =  Math.sin(angle) + parent.getRandom().nextGaussian() * speedMultiplier;
        world.addParticle(particleData, parent.getX() + offsetX, parent.getY() + offsetY, parent.getZ() + offsetZ, speedX, ySpeed, speedZ);
    }
}