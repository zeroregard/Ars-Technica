package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ItemProjectileEntity;
import net.mcreator.ars_technica.common.helpers.ConsumptionHelper;
import net.mcreator.ars_technica.common.helpers.FluidHelper;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

import static net.mcreator.ars_technica.common.helpers.RecipeHelpers.getSpoutFillingRecipe;

public class EffectTelefeast extends AbstractEffect {
    public static final EffectTelefeast INSTANCE = new EffectTelefeast();

    private EffectTelefeast() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_telefeast"), "Telefeast");
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var target = rayTraceResult.getEntity();

        handleEntity(shooter, target, world);
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var pos = rayTraceResult.getBlockPos();
        BlockEntity be = world.getBlockEntity(pos);
        boolean canUse = spellStats.isSensitive(); // Will try to 'use' an item even if it's not a drink/food
        boolean forwardItem = spellStats.getBuffCount(AugmentPierce.INSTANCE) > 0;

        Direction dir = rayTraceResult.getDirection();
        Vec3 inverseDirection = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ()).scale(-1).normalize();

        if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()) {
            IFluidHandler fluidHandler = FluidHelper.getHandlerFromCap(pos, world, 0);
            if (fluidHandler != null) {
                handleFluid(shooter, fluidHandler.getFluidInTank(0), fluidHandler, world, inverseDirection, be.getBlockPos(), forwardItem);
            }
        } else if (be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            IItemHandler itemHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (itemHandler != null) {
                handleItem(shooter, itemHandler, world, inverseDirection, be.getBlockPos(), canUse, forwardItem);
            }
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
                if(forwardItem) {
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
    public String getBookDescription() {
        return "Consumes the first edible/potion, or some amount of liquid, found in the container/tank this was cast on. Augment with Sensitive for also targeting 'usable' items";
    }
}
