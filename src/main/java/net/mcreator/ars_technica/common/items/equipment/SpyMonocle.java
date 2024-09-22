package net.mcreator.ars_technica.common.items.equipment;

import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import net.mcreator.ars_technica.client.TooltipUtils;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

import static com.hollingsworth.arsnouveau.client.registry.ModKeyBindings.HEAD_CURIO_HOTKEY;

public class SpyMonocle extends Item {
    private boolean isZoomed = false;
    public static final float ZOOM_FOV_MODIFIER = 0.1F;
    public static final float ZOOM_SENSITIVITY_MODIFIER = 0.05F;
    private static final String ZOOM_TAG = "Zoomed";
    private double previousSensitivity = 0.0;

    public SpyMonocle(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.translatable("item.ars_technica.spy_monocle.tooltip", KeyMapping.createNameSupplier(ModKeyBindings.HEAD_CURIO_HOTKEY.getName()).get()));
    }
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (world.isClientSide && entity instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findEquippedCurio(stack.getItem(), player).isPresent()) {
                if (HEAD_CURIO_HOTKEY.isDown() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                    setZoomState(stack, true, player);
                } else {
                    setZoomState(stack, false, player);
                }
            }
        }
    }

    private void setZoomState(ItemStack stack, boolean zoomed, Player player) {
        boolean currentlyZoomed = stack.getOrCreateTag().getBoolean(ZOOM_TAG);

        if (currentlyZoomed != zoomed) {
            stack.getOrCreateTag().putBoolean(ZOOM_TAG, zoomed);
            if (zoomed) {
                player.playSound(ArsTechnicaModSounds.SPY_MONOCLE_USE.get(), 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0f, 1.0f);
            }
            adjustMouseSensitivity(player, zoomed);
        }
    }

    public static boolean isZoomed(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(ZOOM_TAG);
    }

    private void adjustMouseSensitivity(Player player, boolean zooming) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == player && zooming) {
            if (previousSensitivity == 0.0F) {
                previousSensitivity = mc.options.sensitivity().get();
            }
            mc.options.sensitivity().set(previousSensitivity * ZOOM_SENSITIVITY_MODIFIER);
        } else {
            mc.options.sensitivity().set(previousSensitivity);
            previousSensitivity = 0.0F;
        }
    }
}