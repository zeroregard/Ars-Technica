package net.mcreator.ars_technica.mixin.Wrench;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionMovementSetting;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.mounted.MinecartContraptionItem;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.utility.Lang;
import net.mcreator.ars_technica.common.items.equipment.RunicSpanner;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import com.simibubi.create.AllMovementBehaviours;

import com.simibubi.create.content.contraptions.ContraptionData;

import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;

import com.simibubi.create.foundation.advancement.AllAdvancements;
import net.minecraft.network.chat.MutableComponent;

@Mixin(MinecartContraptionItem.class)
public class MinecartContraptionItemMixin {

    @Inject(method = "wrenchCanBeUsedToPickUpMinecartContraptions", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void wrenchFix(PlayerInteractEvent.EntityInteract event, CallbackInfo cir) {
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        if (player == null || entity == null)
            return;

        ItemStack wrench = player.getItemInHand(event.getHand());
        if (!(wrench.getItem() instanceof RunicSpanner) && !(wrench.getItem() instanceof WrenchItem)) {
            return;
        }

        // Original logic of the method
        if (entity instanceof AbstractContraptionEntity)
            entity = entity.getVehicle();
        if (!(entity instanceof AbstractMinecart))
            return;
        if (!entity.isAlive())
            return;
        if (player instanceof DeployerFakePlayer dfp && dfp.onMinecartContraption)
            return;
        AbstractMinecart cart = (AbstractMinecart) entity;
        AbstractMinecart.Type type = cart.getMinecartType();
        if (type != AbstractMinecart.Type.RIDEABLE && type != AbstractMinecart.Type.FURNACE && type != AbstractMinecart.Type.CHEST)
            return;
        List<Entity> passengers = cart.getPassengers();
        if (passengers.isEmpty() || !(passengers.get(0) instanceof OrientedContraptionEntity))
            return;
        OrientedContraptionEntity oce = (OrientedContraptionEntity) passengers.get(0);
        Contraption contraption = oce.getContraption();

        if (ContraptionMovementSetting.isNoPickup(contraption.getBlocks()
                .values())) {
            player.displayClientMessage(Lang.translateDirect("contraption.minecart_contraption_illegal_pickup")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        if (event.getLevel().isClientSide) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            cir.cancel();
            return;
        }

        contraption.stop(event.getLevel());

        for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair : contraption.getActors())
            if (AllMovementBehaviours.getBehaviour(pair.left.state()) instanceof PortableStorageInterfaceMovement psim)
                psim.reset(pair.right);

        ItemStack generatedStack = MinecartContraptionItem.create(type, oce).setHoverName(entity.getCustomName());

        if (ContraptionData.isTooLargeForPickup(generatedStack.serializeNBT())) {
            MutableComponent message = Lang.translateDirect("contraption.minecart_contraption_too_big")
                    .withStyle(ChatFormatting.RED);
            player.displayClientMessage(message, true);
            return;
        }

        if (contraption.getBlocks()
                .size() > 200)
            AllAdvancements.CART_PICKUP.awardTo(player);

        player.getInventory()
                .placeItemBackInInventory(generatedStack);
        oce.discard();
        entity.discard();
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        cir.cancel();
    }
}
