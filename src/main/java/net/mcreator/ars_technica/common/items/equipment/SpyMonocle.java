package net.mcreator.ars_technica.common.items.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import static com.hollingsworth.arsnouveau.client.registry.ModKeyBindings.HEAD_CURIO_HOTKEY;

public class SpyMonocle extends Item {
    public boolean isZoomed = false;
    private static final int USE_DURATION = 1200;
    private static final float ZOOM_FOV_MODIFIER = 0.1F;
    public SpyMonocle(Properties properties) {

        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (world.isClientSide && entity instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findEquippedCurio(stack.getItem(), player).isPresent()) {
                if (HEAD_CURIO_HOTKEY.isDown()) {
                   zoom(player, world);
                } else {
                    stopZoom(player, world);
                }
            }

        }
    }

    private void zoom(Player p, Level l) {
        if (isZoomed) {
            return;
        }
        isZoomed = true;
        p.playSound(SoundEvents.SPYGLASS_USE, 1.0f, 1.0f);
    }

    private void stopZoom(Player p, Level l) {
        if (!isZoomed) {
            return;
        }
        isZoomed = false;
        p.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0f, 1.0f);
    }
}