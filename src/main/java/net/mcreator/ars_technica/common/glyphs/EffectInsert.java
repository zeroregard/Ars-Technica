package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;

import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.helpers.StorageHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EffectInsert extends AbstractItemResolveEffect {
    public static final EffectInsert INSTANCE = new EffectInsert();

    private EffectInsert() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_insert"), "Insert");
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  LivingEntity shooter,
                                  SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double expansion = 2 + spellStats.getAoeMultiplier();
        List<BlockEntity> containers = getContainersInArea(pos, world, (int) expansion);
        boolean split = !spellStats.getAugments().stream().filter(x -> x == AugmentSplit.INSTANCE).findFirst().isEmpty();
        processItemEntities(world, entityList, containers, split);
    }

    public void processItemEntities(Level world, List<ItemEntity> entityList, List<BlockEntity> containers, boolean split) {
        List<BlockPos> containerPosTargets = new ArrayList<>();
        for (BlockEntity container : containers) {
            containerPosTargets.add(container.getBlockPos());
        }

        Iterator<ItemEntity> itemIterator = entityList.iterator();

        while (itemIterator.hasNext()) {
            ItemEntity itemEntity = itemIterator.next();
            ItemStack stack = itemEntity.getItem();

            if (stack.isEmpty()) {
                itemIterator.remove();
                continue;
            }
            if (split) {
                processItemsSplit(stack, itemEntity, itemIterator, world, containerPosTargets);
            } else {
                processItemsNonSplit(stack, itemEntity, itemIterator, world, containerPosTargets);
            }
        }
    }

    private void processItemsNonSplit(ItemStack stack, ItemEntity itemEntity, Iterator<ItemEntity> itemIterator, Level world, List<BlockPos> containerPosTargets) {
        BlockPos bestPos = StorageHelpers.getValidStorePos(world, containerPosTargets, stack);
        if (bestPos != null) {
            BlockEntity bestContainer = world.getBlockEntity(bestPos);
            if (bestContainer != null) {
                IItemHandler itemHandler = StorageHelpers.getItemCapFromTile(bestContainer);
                if (itemHandler != null) {
                    ItemStack remaining = ItemHandlerHelper.insertItemStacked(itemHandler, stack, false);
                    if (remaining.isEmpty()) {
                        itemIterator.remove();
                        itemEntity.discard();
                    } else {
                        itemEntity.setItem(remaining);
                    }
                }
            }
        }
    }

    private void processItemsSplit(ItemStack stack, ItemEntity itemEntity, Iterator<ItemEntity> itemIterator, Level world, List<BlockPos> containerPosTargets) {
        List<BlockPos> validPositions = StorageHelpers.getValidStorePositions(world, containerPosTargets, stack);
        int totalItems = stack.getCount();
        int itemsPerContainer = totalItems / validPositions.size();
        int remainder = totalItems % validPositions.size();
        int itemsInserted = 0;

        for (BlockPos pos : validPositions) {
            BlockEntity container = world.getBlockEntity(pos);
            if (container != null) {
                IItemHandler itemHandler = StorageHelpers.getItemCapFromTile(container);
                if (itemHandler != null) {
                    int itemsToInsert = itemsPerContainer + (remainder > 0 ? 1 : 0);
                    ItemStack toInsert = new ItemStack(stack.getItem(), itemsToInsert);
                    ItemStack remaining = ItemHandlerHelper.insertItemStacked(itemHandler, toInsert, false);
                    int inserted = itemsToInsert - remaining.getCount();
                    itemsInserted += inserted;
                    remainder -= (inserted > 0) ? 1 : 0;
                }
            }
        }

        if (itemsInserted >= totalItems) {
            itemIterator.remove();
            itemEntity.discard();
        } else {
            stack.setCount(totalItems - itemsInserted);
            itemEntity.setItem(stack);
        }
    }

    public List<BlockEntity> getContainersInArea(BlockPos pos, Level world, int expansion) {
        List<BlockEntity> containers = new ArrayList<>();
        BlockPos minPos = pos.offset(-expansion, -expansion, -expansion);
        BlockPos maxPos = pos.offset(expansion, expansion, expansion);
        for (BlockPos currentPos : BlockPos.betweenClosed(minPos, maxPos)) {
            BlockEntity tileEntity = world.getBlockEntity(currentPos);
            if (tileEntity instanceof Container) {
                containers.add(tileEntity);
            }
        }
        return containers;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentSplit.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Inserts nearby items into nearby containers in a medium radius where this spell is activated. The range may be expanded with AOE.";
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

}