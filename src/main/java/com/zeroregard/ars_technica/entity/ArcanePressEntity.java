package com.zeroregard.ars_technica.entity;

import com.simibubi.create.AllSoundEvents;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.util.Color;

import java.util.List;

public class ArcanePressEntity extends AbstractDensificationEntity {

    public ArcanePressEntity(Vec3 position, Level world, int maxAmountToPress, float speed, Color color, List<ItemEntity> pressableEntities) {
        super(EntityRegistry.ARCANE_PRESS_ENTITY.get(), position, world, maxAmountToPress, speed, color, pressableEntities);
    }

    public ArcanePressEntity(EntityType<ArcanePressEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void process(ItemEntity item) {
        press(item);
    }

    private void press(ItemEntity item) {
        ItemStack currentStack = item.getItem();
        if (currentStack.getCount() > 0) {
            var pressingRecipe = RecipeHelpers.getPressingRecipeForItemStack(currentStack, world);

            if (pressingRecipe.isEmpty()) {
                if (currentItem != null) {
                    processableEntities.remove(currentItem);
                    currentItem = null;
                }
                return;
            }

            if (currentItem != null) {
                var currentPos = currentItem.getPosition(1.0f);
                setPos(currentPos.add(0, 1f, 0));
            }

            RegistryAccess registryAccess = world.registryAccess();
            var recipe = pressingRecipe.get().value();
            ItemStack pressedStack = recipe.getResultItem(registryAccess);
            pressedStack.setCount(1);

            growOutput(item, pressedStack);

            currentStack.shrink(1);

            amountProcessed++;

            AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(world, blockPosition(), .5f,
                    .75f + (speed / 16));
        }

        if (currentStack.getCount() <= 0 && currentItem != null) {
            currentItem = null;
        }
    }

    @Override
    protected boolean canProcessStack(ItemStack stack) {
        return RecipeHelpers.getPressingRecipeForItemStack(stack, world).isPresent();
    }
}
