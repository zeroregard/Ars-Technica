package com.zeroregard.ars_technica.network;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.api.IRuneTileModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TicksUntilChargePacket extends AbstractPacket {

    private final int ticksUntilChargeCount;
    private final BlockPos blockPos;

    public TicksUntilChargePacket(int ticksUntilChargeCount, BlockPos blockPos) {
        this.ticksUntilChargeCount = ticksUntilChargeCount;
        this.blockPos = blockPos;
    }

    public static void encode(TicksUntilChargePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.ticksUntilChargeCount);
        buf.writeBlockPos(msg.blockPos);
    }

    public static TicksUntilChargePacket decode(FriendlyByteBuf buf) {
        int ticks = buf.readInt();
        BlockPos pos = buf.readBlockPos();
        return new TicksUntilChargePacket(ticks, pos);
    }

    /**
     * This method is called when the packet is received on the server.
     */
    @Override
    public void onServerReceived(@Nonnull MinecraftServer server, @Nonnull ServerPlayer player) {
        if (player == null) return;
        Level level = player.level();
        if (level.getBlockEntity(this.blockPos) instanceof RuneTile runeTile) {
            if (runeTile instanceof IRuneTileModifier customizable) {
                customizable.setTicksUntilChargeCount(this.ticksUntilChargeCount);
                runeTile.setChanged();
            }
        }
    }

    public static final CustomPacketPayload.Type<TicksUntilChargePacket> TYPE =
            new CustomPacketPayload.Type<>(ArsTechnica.prefix("ticks_until_charge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TicksUntilChargePacket> CODEC =
            StreamCodec.ofMember(TicksUntilChargePacket::encode, TicksUntilChargePacket::decode);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
