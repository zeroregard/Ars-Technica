package com.zeroregard.ars_technica.network;

import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.api.ITechnomancerAware;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class TechnomancerNearbyPacket extends AbstractPacket {

    private final boolean technomancerNearby;
    private final BlockPos blockPos;

    public TechnomancerNearbyPacket(boolean technomancerNearby, BlockPos blockPos) {
        this.technomancerNearby = technomancerNearby;
        this.blockPos = blockPos;
    }


    public static void encode(TechnomancerNearbyPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.technomancerNearby);
        buf.writeBlockPos(msg.blockPos);
    }

    public static TechnomancerNearbyPacket decode(FriendlyByteBuf buf) {
        boolean technomancerNearby = buf.readBoolean();
        BlockPos blockPos = buf.readBlockPos();
        return new TechnomancerNearbyPacket(technomancerNearby, blockPos);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        BlockEntity blockEntity = minecraft.level.getBlockEntity(this.blockPos);

        if (blockEntity instanceof ITechnomancerAware technomancerAware) {
            technomancerAware.setTechnomancerNearby(this.technomancerNearby);
        }
    }


    public static final CustomPacketPayload.Type<TechnomancerNearbyPacket> TYPE =
            new CustomPacketPayload.Type<>(ArsTechnica.prefix("technomancer_nearby"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TechnomancerNearbyPacket> CODEC =
            StreamCodec.ofMember(TechnomancerNearbyPacket::encode, TechnomancerNearbyPacket::decode);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
