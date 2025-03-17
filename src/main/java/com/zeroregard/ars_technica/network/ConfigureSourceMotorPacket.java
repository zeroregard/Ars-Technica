package com.zeroregard.ars_technica.network;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.block.SourceMotorBlockEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureSourceMotorPacket extends BlockEntityConfigurationPacket<SourceMotorBlockEntity> {
    public static final StreamCodec<ByteBuf, Integer> INT_CODEC = new IntCodec();
    public static final StreamCodec<ByteBuf, ConfigureSourceMotorPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, packet -> packet.pos,
                    INT_CODEC, packet -> packet.suRatio,
                    ConfigureSourceMotorPacket::new
            );
    private int suRatio;

    public ConfigureSourceMotorPacket(BlockPos pos, int suRatio) {
        super(pos);
        this.suRatio = suRatio;
    }

    @Override
    protected void applySettings(ServerPlayer player, SourceMotorBlockEntity sourceMotorBlockEntity) {
        sourceMotorBlockEntity.setGeneratedStressUnitsRatio(suRatio);
        sourceMotorBlockEntity.sendData();
    }

    @Override
    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return ATPackets.CONFIGURE_SOURCE_MOTOR;
    }

    public static class IntCodec implements StreamCodec<ByteBuf, Integer> {
        @Override
        public Integer decode(ByteBuf buf) {
            return buf.readInt();
        }

        @Override
        public void encode(ByteBuf buf, Integer value) {
            buf.writeInt(value);
        }
    }

}

