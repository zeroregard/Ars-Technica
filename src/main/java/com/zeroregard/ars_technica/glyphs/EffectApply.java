package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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
            if (handleBlockApplication(pos, world, shooter, spellStats, spellContext, resolver)) {
                return;
            }
        }
        
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    private boolean handleBlockApplication(BlockPos centerPos, Level world, @Nullable LivingEntity shooter,
                                           SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(shooter instanceof Player player)) {
            return false;
        }

        ItemStack offhandItem = player.getOffhandItem();
        if (offhandItem.isEmpty()) {
            return false;
        }

        int aoeBuff = (int)Math.round(spellStats.getAoeMultiplier());
        int expansion = Math.max(0, aoeBuff);
        
        int applicationsPerformed = 0;
        
        BlockPos minPos = centerPos.offset(-expansion, -expansion, -expansion);
        BlockPos maxPos = centerPos.offset(expansion, expansion, expansion);
        
        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            if (offhandItem.isEmpty()) {
                break;
            }
            
            BlockState targetBlock = world.getBlockState(pos);
            var recipe = getApplicationRecipe(offhandItem, targetBlock, world);
            if (recipe.isEmpty()) {
                continue;
            }

            var result = recipe.get().value().getResultItem(world.registryAccess());
            if (result.isEmpty()) {
                continue;
            }

            offhandItem.shrink(1);

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
        if (!(shooter instanceof Player player)) {
            return;
        }

        ItemStack offhandItem = player.getOffhandItem();
        if (offhandItem.isEmpty()) {
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
            var recipe = getApplicationRecipe(offhandItem, itemStack, world);
            
            if (recipe.isPresent()) {
                var result = recipe.get().value().getResultItem(world.registryAccess());
                if (!result.isEmpty()) {
                    int remainingToApply = maxAmountToApply - totalApplied;
                    int stackSize = itemStack.getCount();
                    int applicationsToThisStack = Math.min(remainingToApply, stackSize);
                    applicationsToThisStack = Math.min(applicationsToThisStack, offhandItem.getCount());
                    
                    if (applicationsToThisStack > 0) {
                        offhandItem.shrink(applicationsToThisStack);
                        
                        itemStack.shrink(applicationsToThisStack);
                        if (itemStack.getCount() <= 0) {
                            itemEntity.discard();
                        }

                        for (int i = 0; i < applicationsToThisStack; i++) {
                            ItemEntity resultEntity = new ItemEntity(world, 
                                itemEntity.getX() + (world.random.nextFloat() - 0.5f) * 0.2f, 
                                itemEntity.getY(), 
                                itemEntity.getZ() + (world.random.nextFloat() - 0.5f) * 0.2f, 
                                result.copy());
                            world.addFreshEntity(resultEntity);
                        }

                        world.playSound(null, itemEntity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 
                            0.6f, 1.0f + (world.random.nextFloat() - 0.5f) * 0.4f);
                        
                        totalApplied += applicationsToThisStack;
                        
                        if (offhandItem.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private Optional<RecipeHolder<Recipe<RecipeInput>>> getApplicationRecipe(ItemStack applyItem, ItemStack target, Level world) {
        return RecipeHelpers.getItemApplicationRecipe(applyItem, target, world);
    }

    private Optional<RecipeHolder<Recipe<RecipeInput>>> getApplicationRecipe(ItemStack applyItem, BlockState target, Level world) {
        ItemStack targetItem = new ItemStack(target.getBlock().asItem());
        if (targetItem.isEmpty() || targetItem.getItem() == net.minecraft.world.item.Items.AIR) {
            return Optional.empty();
        }
        return getApplicationRecipe(applyItem, targetItem, world);
    }

    @Override
    public int getDefaultManaCost() {
        return 80;
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the amount of items that can be applied to and the area of blocks affected");
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public String getBookDescription() {
        return "Uses the item in your offhand to apply to blocks or items, such as applying andesite alloy to stripped logs to create casings";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }
} 