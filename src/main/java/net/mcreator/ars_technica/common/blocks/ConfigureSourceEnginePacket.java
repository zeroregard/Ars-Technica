package net.mcreator.ars_technica.common.blocks;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureSourceEnginePacket extends BlockEntityConfigurationPacket<SourceEngineBlockEntity> {

    private int speed;
    private int suRatio;

    public ConfigureSourceEnginePacket(BlockPos pos, int speed, int suRatio) {
        super(pos);
        this.speed = speed;
        this.suRatio = suRatio;
    }

    public ConfigureSourceEnginePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        speed = buffer.readInt();
        suRatio = buffer.readInt();
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeInt(speed);
        buffer.writeInt(suRatio);
    }

    @Override
    protected void applySettings(SourceEngineBlockEntity be) {
        be.setGeneratedSpeed(speed);
        be.setGeneratedStressUnitsRatio(suRatio);
    }

}
