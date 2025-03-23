package com.zeroregard.ars_technica.item;

import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.zeroregard.ars_technica.registry.DataComponentRegistry;
import com.zeroregard.ars_technica.registry.SoundRegistry;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

import static com.hollingsworth.arsnouveau.client.registry.ModKeyBindings.HEAD_CURIO_HOTKEY;

public class SpyMonocle extends Item {
    private boolean isZoomed = false;
    public static final float ZOOM_FOV_MODIFIER = 0.1F;
    public static final float ZOOM_SENSITIVITY_MODIFIER = 0.05F;
    private static final String ZOOM_TAG = "Zoomed";
    private Double previousSensitivity = null;

    public SpyMonocle(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.ars_technica.spy_monocle.tooltip", KeyMapping.createNameSupplier(ModKeyBindings.HEAD_CURIO_HOTKEY.getName()).get()));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (world.isClientSide && entity instanceof Player player) {
            var curioSlot = CuriosApi.getCuriosInventory(player).flatMap(handler -> handler.findFirstCurio(stack.getItem()));
            if(curioSlot.isPresent()) {
                setZoomState(stack, HEAD_CURIO_HOTKEY.isDown() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON, player);
            }
        }
    }

    private void setZoomState(ItemStack stack, boolean zoomed, Player player) {
        boolean currentlyZoomed = isZoomed(stack);

        if (currentlyZoomed != zoomed) {
            DataComponentPatch patch = DataComponentPatch.builder()
                    .set(DataComponentRegistry.ZOOMED.get(), zoomed)
                    .build();
            stack.applyComponents(patch);
            if (zoomed) {
                player.playSound(SoundRegistry.SPY_MONOCLE_USE.get(), 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0f, 1.0f);
            }
            adjustMouseSensitivity(player, zoomed);
        }
    }

    public static boolean isZoomed(ItemStack stack) {
        var components = stack.getComponents();
        boolean currentlyZoomed = components.getOrDefault(DataComponentRegistry.ZOOMED.get(), false);
        return currentlyZoomed;
    }

    private void adjustMouseSensitivity(Player player, boolean zooming) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == player) {
            if(zooming) {
                if (previousSensitivity == null) {
                    previousSensitivity = mc.options.sensitivity().get();
                }
                mc.options.sensitivity().set(previousSensitivity * ZOOM_SENSITIVITY_MODIFIER);
            } else if (previousSensitivity != null) {
                mc.options.sensitivity().set(previousSensitivity);
                previousSensitivity = null;
            }
        }
    }
}