package net.mcreator.ars_technica.common.glyphs;

import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
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
        LivingEntity consumer = shooter;

        handleEntity(shooter, consumer, target, world);
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var pos = rayTraceResult.getBlockPos();
        BlockEntity be = world.getBlockEntity(pos);
        LivingEntity consumer = shooter;
        boolean canUse = spellStats.isSensitive(); // Will try to 'use' an item even if it's not a drink/food

        if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()) {
            IFluidHandler fluidHandler = StarbyFluidBehavior.getHandlerFromCap(pos, world, 0);
            if (fluidHandler != null) {
                handleFluid(shooter, consumer, fluidHandler.getFluidInTank(0), fluidHandler, world);
            }
        } else if (be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            IItemHandler itemHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (itemHandler != null) {
                handleItem(shooter, consumer, itemHandler, world, canUse);
            }
        }
    }

    private void handleEntity(LivingEntity caster, LivingEntity consumer, Entity target, Level world) {
        if(target instanceof Cow) {
            var itemResult = new ItemStack(Items.MILK_BUCKET);
            drinkSound(world, consumer);
            itemResult.finishUsingItem(world, consumer);
        }
    }

    private void handleItem(LivingEntity caster, LivingEntity consumer, IItemHandler itemHandler, Level world, boolean canUse) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                if(tryUseConsumableItem(consumer, itemStack, world, canUse)) {
                    break;
                }

                if (tryUseEdibleItem(consumer, itemStack, world)) {
                    break;
                }
            }
        }
    }

    private boolean tryUseConsumableItem(LivingEntity consumer, ItemStack itemStack, Level world, boolean canUse) {
        if(!canUse) {
            return false;
        }

        // While LivingEntities have interaction hands, only 'Player' can be passed for the 'use' function
        if(consumer instanceof Player player) {
            InteractionHand temporaryHand = InteractionHand.MAIN_HAND;
            ItemStack originalItem = player.getItemInHand(temporaryHand);

            try {
                player.setItemInHand(temporaryHand, itemStack);
                var result = itemStack.use(world, player, temporaryHand).getResult();
                if (result != InteractionResult.FAIL) {
                    return true;
                }
            } finally {
                player.setItemInHand(temporaryHand, originalItem);
            }
            return false;
        }
        return false;
    }

    private boolean tryUseEdibleItem(LivingEntity consumer, ItemStack itemStack, Level world) {
        if (itemStack.isEdible()) {
            consumer.eat(world, itemStack);
            world.playSound(null, consumer.getX(), consumer.getY(), consumer.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);
            return true;
        } else if ((itemStack.getItem() instanceof PotionItem || itemStack.getUseAnimation() == UseAnim.DRINK)) {
            itemStack.finishUsingItem(world, consumer);
            world.playSound(null, consumer.getX(), consumer.getY(), consumer.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    private void handleFluid(LivingEntity caster, LivingEntity consumer, FluidStack fluid, IFluidHandler handler, Level world) {
        if (fluid.getFluid() == Fluids.LAVA) {
            Holder<DamageType> lavaDamageType = world.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.LAVA);
            DamageSource lavaDamage = new DamageSource(lavaDamageType, caster, caster);
            handler.drain(250, IFluidHandler.FluidAction.EXECUTE);
            drinkSound(world, consumer);
            consumer.hurt(lavaDamage, 50);
            return;
        }

        var bottleRecipe = getSpoutFillingRecipe(fluid, new ItemStack(Items.GLASS_BOTTLE), world);
        if (bottleRecipe.isPresent() && handler.getFluidInTank(0).getAmount() >= bottleRecipe.get().fluidAmount) {
            ItemStack outputItem = bottleRecipe.get().output.copy();
            if (outputItem.getItem() instanceof PotionItem || outputItem.getUseAnimation() == UseAnim.DRINK) {
                handler.drain( bottleRecipe.get().fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                outputItem.getItem().finishUsingItem(outputItem, world, consumer);
                drinkSound(world, consumer);
                return;
            }
        }

        var bucketRecipe = getSpoutFillingRecipe(fluid, new ItemStack(Items.BUCKET), world);
        if (bucketRecipe.isPresent() && handler.getFluidInTank(0).getAmount() >= bucketRecipe.get().fluidAmount) {
            ItemStack outputItem = bucketRecipe.get().output.copy();
            if (outputItem.getUseAnimation() == UseAnim.DRINK) {
                handler.drain( bucketRecipe.get().fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                outputItem.getItem().finishUsingItem(outputItem, world, consumer);
                drinkSound(world, consumer);
                return;
            }
        }


    }

    private void drinkSound(Level world, LivingEntity consumer) {
        world.playSound(null, consumer.getX(), consumer.getY(), consumer.getZ(),
                SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    protected int getDefaultManaCost() {
        return 10;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentSensitive.INSTANCE);
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
