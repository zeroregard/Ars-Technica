package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.zeroregard.ars_technica.entity.ItemProjectileEntity;
import com.zeroregard.ars_technica.helpers.ConsumptionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;
import static com.zeroregard.ars_technica.helpers.ConsumptionHelper.isDrink;
import static com.zeroregard.ars_technica.helpers.ConsumptionHelper.isFood;

public class EffectTelefeast extends AbstractEffect {
    public static EffectTelefeast INSTANCE = new EffectTelefeast(prefix("glyph_telefeast"), "Telefeast");

    private EffectTelefeast(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var pos = rayTraceResult.getBlockPos();
        BlockEntity be = world.getBlockEntity(pos);
        if(be == null) {
            return;
        }
        BlockState bs = be.getBlockState();
        boolean canUse = spellStats.isSensitive();
        boolean forwardItem = spellStats.getBuffCount(AugmentPierce.INSTANCE) > 0;

        Direction dir = rayTraceResult.getDirection();
        Vec3 inverseDirection = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ()).scale(-1).normalize();

        var fluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(world, pos, bs, be, null);
        var itemHandler = Capabilities.ItemHandler.BLOCK.getCapability(world, pos, bs, be, null);

        if (fluidHandler != null) {
            handleFluid(shooter, fluidHandler.getFluidInTank(0), fluidHandler, world, inverseDirection, be.getBlockPos(), forwardItem);
        } else if (itemHandler != null) {
            handleItem(shooter, itemHandler, world, inverseDirection, be.getBlockPos(), canUse, forwardItem);
        }
    }

    private void handleEntity(LivingEntity caster, Entity target, Level world) {
        if(target instanceof Cow) {
            var itemResult = new ItemStack(Items.MILK_BUCKET);
            ConsumptionHelper.playSound(SoundEvents.GENERIC_DRINK, world, caster);
            itemResult.finishUsingItem(world, caster);
        }
    }

    private void handleItem(LivingEntity caster, IItemHandler itemHandler, Level world, Vec3 direction, BlockPos position, boolean canUse, boolean forwardItem) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                if(forwardItem && (isFood(itemStack, null) || isDrink(itemStack))) {
                    ItemStack extractedItem = itemHandler.extractItem(i, 1, false);
                    forwardItem(world, extractedItem, direction, position.getCenter());
                    break;
                }

                if(ConsumptionHelper.tryUseConsumableItem(caster, itemStack, world, canUse)) {
                    break;
                }

                if (ConsumptionHelper.tryUseEdibleItem(caster, itemStack, world)) {
                    break;
                }
            }
        }
    }

    private void handleFluid(LivingEntity caster, FluidStack fluid, IFluidHandler handler, Level world, Vec3 direction, BlockPos position, boolean forwardItem) {
        if (fluid.getFluid() == Fluids.LAVA && !forwardItem) {
            Holder<DamageType> lavaDamageType = world.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.LAVA);
            DamageSource lavaDamage = new DamageSource(lavaDamageType, caster, caster);
            handler.drain(250, IFluidHandler.FluidAction.EXECUTE);
            caster.hurt(lavaDamage, 50);
            return;
        }

        ItemStack outputItem = null;

        var bottleRecipe = getSpoutFillingRecipe(fluid, new ItemStack(Items.GLASS_BOTTLE), world);
        if (bottleRecipe.isPresent() && handler.getFluidInTank(0).getAmount() >= bottleRecipe.get().fluidAmount) {
            outputItem = bottleRecipe.get().output.copy();
            if (outputItem.getItem() instanceof PotionItem || outputItem.getUseAnimation() == UseAnim.DRINK) {
                handler.drain( bottleRecipe.get().fluidAmount, IFluidHandler.FluidAction.EXECUTE);
            } else {
                outputItem = null;
            }
        }

        if(outputItem == null) {
            var bucketRecipe = getSpoutFillingRecipe(fluid, new ItemStack(Items.BUCKET), world);
            if (bucketRecipe.isPresent() && handler.getFluidInTank(0).getAmount() >= bucketRecipe.get().fluidAmount) {
                outputItem = bucketRecipe.get().output.copy();
                if (outputItem.getUseAnimation() == UseAnim.DRINK) {
                    handler.drain( bucketRecipe.get().fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                } else {
                    outputItem = null;
                }
            }
        }

        if(outputItem != null) {
            if(!forwardItem) {
                ConsumptionHelper.tryUseEdibleItem(caster, outputItem, world);
            } else {
                forwardItem(world, outputItem, direction, position.getCenter());
            }
        }


    }

    private void forwardItem(Level world, ItemStack item, Vec3 direction, Vec3 position) {
        if (item.isEmpty()) {
            return;
        }
        ItemProjectileEntity projectile = new ItemProjectileEntity(world, position.add(0, -0.5, 0), direction, item);
        world.addFreshEntity(projectile);
    }

    @Override
    protected int getDefaultManaCost() {
        return 10;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentSensitive.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }


    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Will try to 'use' an item even if it's not a drink/food (for example experience gems)");
        map.put(AugmentPierce.INSTANCE, "Changes to 'pierce' through the container, carrying the consumable in a magic floating bubble");
    }

    @Override
    public String getBookDescription() {
        return "Consumes the first edible/potion, or some amount of liquid, found in the container/tank this was cast on.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    private static Optional<FillingResult> getSpoutFillingRecipe(FluidStack fluidIngredient, ItemStack itemIngredient, Level world) {
        RecipeManager recipeManager = world.getRecipeManager();
        var allFillingRecipes = recipeManager.getAllRecipesFor(AllRecipeTypes.FILLING.getType());

        // Check for registered FillingRecipes
        var staticRecipe = allFillingRecipes
                .stream()
                .map(RecipeHolder::value)
                .filter(FillingRecipe.class::isInstance)
                .map(FillingRecipe.class::cast)
                .filter(recipe -> recipe.getFluidIngredients().get(0).getMatchingFluidStacks().stream()
                        .anyMatch(stack -> stack.getFluid().isSame(fluidIngredient.getFluid())))
                .filter(recipe -> recipe.getIngredients().get(0).getItems()[0].getItem() == itemIngredient.getItem())
                .findFirst();

        if (staticRecipe.isPresent()) {
            var recipe = staticRecipe.get();
            var result = new FillingResult(recipe.getResultItem(world.registryAccess()).copy(), recipe.getRequiredFluid().getRequiredAmount());
            return Optional.of(result);
        }

        if(GenericItemFilling.canItemBeFilled(world, itemIngredient)) {
            int requiredAmount = GenericItemFilling.getRequiredAmountForItem(world, itemIngredient, fluidIngredient);
            if (requiredAmount != -1 && requiredAmount <= fluidIngredient.getAmount()) {
                // filling an item will remove from the fluid stack, but we want to set the amount back on the stack
                // and then later drain the storage tank, so we get the updated storage tank visuals
                var itemResult = GenericItemFilling.fillItem(world, requiredAmount, itemIngredient, fluidIngredient);
                fluidIngredient.setAmount(fluidIngredient.getAmount() + requiredAmount);
                var result = new FillingResult(itemResult, requiredAmount);
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    public record FillingResult(ItemStack output, int fluidAmount) {}
}
