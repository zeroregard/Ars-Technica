package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EffectInsert extends AbstractItemResolveEffect {
  public static EffectInsert INSTANCE = new EffectInsert();

  private EffectInsert() {
    super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_insert"), "Insert");
  }

  @Override
  public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
      LivingEntity shooter,
      SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
    double expansion = 2 + spellStats.getAoeMultiplier();
    List<BlockEntity> containers = getContainersInArea(pos, world, (int) expansion);
    processItemEntities(entityList, containers);
  }

  public void processItemEntities(List<ItemEntity> entityList, List<BlockEntity> containers) {
    Iterator<ItemEntity> itemIterator = entityList.iterator();

    for (BlockEntity container : containers) {
      LazyOptional<IItemHandler> capability = container.getCapability(ForgeCapabilities.ITEM_HANDLER, null);

      if (!capability.isPresent()) {
        continue;
      }
      IItemHandler itemHandler = capability.orElse(null);

      while (itemIterator.hasNext()) {
        ItemEntity itemEntity = itemIterator.next();
        ItemStack stack = itemEntity.getItem();

        if (stack.isEmpty()) {
          itemIterator.remove();
          continue;
        }

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(itemHandler, stack, false);

        if (remaining.isEmpty()) {
          itemIterator.remove();
          itemEntity.discard();
        } else {
          itemEntity.setItem(remaining);
        }

        if (isContainerFull(itemHandler)) {
          break;
        }
      }
    }
  }

  private boolean isContainerFull(IItemHandler itemHandler) {
    for (int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack stackInSlot = itemHandler.getStackInSlot(i);
      int slotLimit = itemHandler.getSlotLimit(i);
      if (stackInSlot.isEmpty() || stackInSlot.getCount() < slotLimit) {
        return false;
      }
    }
    return true;
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
    return augmentSetOf(AugmentAOE.INSTANCE);
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