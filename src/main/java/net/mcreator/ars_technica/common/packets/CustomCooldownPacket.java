package net.mcreator.ars_technica.common.packets;

import net.mcreator.ars_technica.common.api.IModifiableCooldown;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CustomCooldownPacket {
    private final int cooldownTicks;
    private final BlockPos blockPos;

    public CustomCooldownPacket(int cooldownTicks, BlockPos blockPos) {
        this.cooldownTicks = cooldownTicks;
        this.blockPos = blockPos;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(cooldownTicks);
        buffer.writeBlockPos(blockPos);
    }

    public static CustomCooldownPacket decode(FriendlyByteBuf buffer) {
        int ticks = buffer.readInt();
        BlockPos pos = buffer.readBlockPos();
        return new CustomCooldownPacket(ticks, pos);
    }

    public static void handle(CustomCooldownPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            Level level = player.level();
            var blockEntity = level.getBlockEntity(packet.blockPos);
            if (blockEntity instanceof IModifiableCooldown customizable) {
                customizable.setCooldownTicks(packet.cooldownTicks);
                blockEntity.setChanged();
            }
        });
        context.setPacketHandled(true);
    }
}