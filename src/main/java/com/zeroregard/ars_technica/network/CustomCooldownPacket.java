package com.zeroregard.ars_technica.network;


import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.api.IModifiableCooldown;

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

public class CustomCooldownPacket extends AbstractPacket {
    private final int cooldownTicks;
    private final BlockPos blockPos;

    public CustomCooldownPacket(int cooldownTicks, BlockPos blockPos) {
        this.cooldownTicks = cooldownTicks;
        this.blockPos = blockPos;
    }

    public static void encode(CustomCooldownPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.cooldownTicks);
        buf.writeBlockPos(msg.blockPos);
    }

    public static CustomCooldownPacket decode(FriendlyByteBuf buf) {
        int ticks = buf.readInt();
        BlockPos pos = buf.readBlockPos();
        return new CustomCooldownPacket(ticks, pos);
    }


    @Override
    public void onServerReceived(@Nonnull MinecraftServer server, @Nonnull ServerPlayer player) {
        if (player == null) return;
        Level level = player.level();
        var blockEntity = level.getBlockEntity(this.blockPos);
        if (blockEntity instanceof IModifiableCooldown customizable) {
            customizable.setCooldownTicks(this.cooldownTicks);
            blockEntity.setChanged();
        }
    }

    public static final CustomPacketPayload.Type<CustomCooldownPacket> TYPE =
            new CustomPacketPayload.Type<>(ArsTechnica.prefix("custom_cooldown"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomCooldownPacket> CODEC =
            StreamCodec.ofMember(CustomCooldownPacket::encode, CustomCooldownPacket::decode);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}