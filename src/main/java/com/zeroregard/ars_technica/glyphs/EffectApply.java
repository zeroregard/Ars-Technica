package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class EffectApply extends AbstractItemResolveEffect {
    public static EffectApply INSTANCE = new EffectApply(prefix("glyph_apply"), "Apply");

    private EffectApply(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats,
                          SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            if (handleBlockApplication(pos, blockHit, world, shooter, spellStats, spellContext, resolver)) {
                return;
            }
        }
        
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    private boolean handleBlockApplication(BlockPos centerPos, BlockHitResult blockHitResult, Level world, @Nullable LivingEntity shooter,
                                           SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        ApplyItemSource applySource = getApplyItemSource(spellContext.getCaster());
        if (applySource.isEmpty()) {
            return false;
        }

        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, centerPos, blockHitResult, aoeBuff, pierceBuff);
        
        int applicationsPerformed = 0;
        
        for (BlockPos pos : posList) {
            ItemStack applyItem = applySource.getItem();
            if (applyItem.isEmpty()) {
                break;
            }
            
            BlockState targetBlock = world.getBlockState(pos);
            var recipe = getApplicationRecipe(applyItem, targetBlock, world);
            if (recipe.isEmpty()) {
                continue;
            }

            var result = recipe.get().value().getResultItem(world.registryAccess());
            if (result.isEmpty()) {
                continue;
            }

            applySource.consumeItem();

            Block resultBlock = Block.byItem(result.getItem());
            if (resultBlock != null && !resultBlock.equals(net.minecraft.world.level.block.Blocks.AIR)) {
                world.setBlock(pos, resultBlock.defaultBlockState(), 3);
            } else {
                ItemEntity resultEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, result.copy());
                world.addFreshEntity(resultEntity);
            }

            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8f, 1.0f + (world.random.nextFloat() - 0.5f) * 0.4f);
            
            applicationsPerformed++;
        }

        return applicationsPerformed > 0;
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {
        ApplyItemSource applySource = getApplyItemSource(spellContext.getCaster());
        if (applySource.isEmpty()) {
            return;
        }

        int aoeBuff = (int)Math.round(spellStats.getAoeMultiplier());
        int maxAmountToApply = 4 * (1 + aoeBuff);

        int totalApplied = 0;
        
        for (ItemEntity itemEntity : entityList) {
            if (totalApplied >= maxAmountToApply) {
                break;
            }

            ItemStack itemStack = itemEntity.getItem();
            ItemStack applyItem = applySource.getItem();
            if (applyItem.isEmpty()) {
                break;
            }

            List<ItemStack> results = new ArrayList<>();
            var seqRecipe = RecipeHelpers.getSequencedAssemblyRecipe(AllRecipeTypes.DEPLOYING.getType(), DeployerApplicationRecipe.class, applyItem, itemStack, world);
            if (seqRecipe.isPresent()) {
                results.addAll(seqRecipe.get().value().rollResults(world.getRandom()));
            } else {
                var recipe = getApplicationRecipe(applyItem, itemStack, world);
                if (recipe.isPresent()) {
                    results.add(recipe.get().value().getResultItem(world.registryAccess()));
                }
            }

            if (!results.isEmpty()) {
                int remainingToApply = maxAmountToApply - totalApplied;
                int stackSize = itemStack.getCount();
                int applicationsToThisStack = Math.min(remainingToApply, stackSize);
                applicationsToThisStack = Math.min(applicationsToThisStack, applySource.getAvailableCount());

                if (applicationsToThisStack > 0) {
                    applySource.consumeItems(applicationsToThisStack);

                    itemStack.shrink(applicationsToThisStack);
                    if (itemStack.getCount() <= 0) {
                        itemEntity.discard();
                    }

                    for (int i = 0; i < applicationsToThisStack; i++) {
                        for (ItemStack result : results) {
                            ItemEntity resultEntity = new ItemEntity(world,
                                    itemEntity.getX() + (world.random.nextFloat() - 0.5f) * 0.2f,
                                    itemEntity.getY(),
                                    itemEntity.getZ() + (world.random.nextFloat() - 0.5f) * 0.2f,
                                    result.copy());
                            world.addFreshEntity(resultEntity);
                        }
                    }

                    world.playSound(null, itemEntity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                            0.6f, 1.0f + (world.random.nextFloat() - 0.5f) * 0.4f);

                    totalApplied += applicationsToThisStack;

                    if (applySource.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    private ApplyItemSource getApplyItemSource(IWrappedCaster caster) {
        if (caster instanceof PlayerCaster playerCaster) {
            return new PlayerApplyItemSource(playerCaster.player);
        }

        var manager = caster.getInvManager();
        if (manager != null) {
            return new InventoryApplyItemSource(manager);
        }

        return new EmptyApplyItemSource();
    }

    private Optional<RecipeHolder<Recipe<RecipeInput>>> getApplicationRecipe(ItemStack applyItem, ItemStack target, Level world) {
        var recipe = RecipeHelpers.getItemApplicationRecipe(applyItem, target, world);
        if (recipe.isPresent()) {
            return recipe;
        }
        
        return RecipeHelpers.getDeployingRecipe(applyItem, target, world);
    }

    private Optional<RecipeHolder<Recipe<RecipeInput>>> getApplicationRecipe(ItemStack applyItem, BlockState target, Level world) {
        ItemStack targetItem = new ItemStack(target.getBlock().asItem());
        if (targetItem.isEmpty() || targetItem.getItem() == net.minecraft.world.item.Items.AIR) {
            return Optional.empty();
        }
        return getApplicationRecipe(applyItem, targetItem, world);
    }

    private abstract static class ApplyItemSource {
        public abstract @NotNull ItemStack getItem();

        public int getAvailableCount() {
            return this.getItem().getCount();
        }

        public abstract void consumeItems(int count);

        public void consumeItem() {
            this.consumeItems(1);
        }

        public boolean isEmpty() {
            return this.getItem().isEmpty();
        }
    }

    private static class PlayerApplyItemSource extends ApplyItemSource {
        private final Player player;

        public PlayerApplyItemSource(Player player) {
            this.player = player;
        }

        @Override
        public @NotNull ItemStack getItem() {
            return player.getOffhandItem();
        }

        @Override
        public void consumeItems(int count) {
            getItem().shrink(count);
        }
    }

    private static class InventoryApplyItemSource extends ApplyItemSource {
        private InventoryManager manager;

        public InventoryApplyItemSource(InventoryManager manager) {
            this.manager = manager;
        }

        @Override
        public @NotNull ItemStack getItem() {
            var ref = this.manager.findItem(s -> true, InteractType.EXTRACT);
            if (ref.isEmpty()) {
                return ItemStack.EMPTY;
            }
            return ref.getHandler().getStackInSlot(ref.getSlot());
        }

        @Override
        public void consumeItems(int count) {
            this.manager.extractItem(s -> true, 1);
        }
    }

    private static class EmptyApplyItemSource extends ApplyItemSource {
        @Override
        public @NotNull ItemStack getItem() { return ItemStack.EMPTY; }
        @Override
        public int getAvailableCount() { return 0; }
        @Override
        public void consumeItem() { }
        @Override
        public void consumeItems(int count) { }
        @Override
        public boolean isEmpty() { return true; }
    }

    @Override
    public int getDefaultManaCost() {
        return 80;
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the amount of items that can be applied to and the area of blocks affected");
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public String getBookDescription() {
        return "Uses the item in your offhand to apply to blocks or items, such as applying andesite alloy to stripped logs to create casings. Also supports deploying items onto other items.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }
} 