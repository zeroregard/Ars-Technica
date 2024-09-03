package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractItemResolveEffect extends AbstractEffect {

  protected AbstractItemResolveEffect(String tag, String description) {
    super(tag, description);
  }

  protected AbstractItemResolveEffect(ResourceLocation resourceLocation, String description) {
    super(resourceLocation, description);
  }

  @Override
  public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats,
      SpellContext spellContext, SpellResolver resolver) {
    Vec3 location = rayTraceResult.getLocation();
    BlockPos pos = BlockPos.containing(location);
    double expansion = 2 + spellStats.getAoeMultiplier();
    Vec3 posVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

    List<ItemEntity> entityList = world.getEntitiesOfClass(ItemEntity.class, new AABB(
        posVec.add(expansion, expansion, expansion), posVec.subtract(expansion, expansion, expansion)));

    onResolveEntities(entityList, pos, posVec, world, shooter, spellStats, spellContext, resolver);
  }

  public abstract void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
      @Nullable LivingEntity shooter,
      SpellStats spellStats,
      SpellContext spellContext, SpellResolver resolver);
}