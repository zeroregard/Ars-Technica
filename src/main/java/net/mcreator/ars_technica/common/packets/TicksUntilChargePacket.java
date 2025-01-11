package net.mcreator.ars_technica.common.packets;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import net.mcreator.ars_technica.common.api.IRuneTileModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TicksUntilChargePacket {
    private final int ticksUntilChargeCount;
    private final BlockPos blockPos;

    public TicksUntilChargePacket(int ticksUntilChargeCount, BlockPos blockPos) {
        this.ticksUntilChargeCount = ticksUntilChargeCount;
        this.blockPos = blockPos;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(ticksUntilChargeCount);
        buffer.writeBlockPos(blockPos);
    }

    public static TicksUntilChargePacket decode(FriendlyByteBuf buffer) {
        int ticks = buffer.readInt();
        BlockPos pos = buffer.readBlockPos();
        return new TicksUntilChargePacket(ticks, pos);
    }

    public static void handle(TicksUntilChargePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            Level level = player.level();
            if (level.getBlockEntity(packet.blockPos) instanceof RuneTile runeTile) {
                if (runeTile instanceof IRuneTileModifier customizable) {
                    customizable.setTicksUntilChargeCount(packet.ticksUntilChargeCount);
                    runeTile.setChanged();
                }
            }
        });
        context.setPacketHandled(true);
    }
}