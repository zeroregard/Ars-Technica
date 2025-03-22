package com.zeroregard.ars_technica.registry;

import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.block.PreciseRelay;
import com.zeroregard.ars_technica.block.SourceMotorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ArsTechnica.MODID);

    public static DeferredBlock<SourceMotorBlock> SOURCE_MOTOR =
            BLOCKS.register("source_motor", () -> new SourceMotorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .forceSolidOn()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.STONE)));

    public static DeferredBlock<PreciseRelay> PRECISE_RELAY =
            BLOCKS.register("precise_relay", () -> new PreciseRelay(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .forceSolidOn()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.STONE)));

    public static void init() { /* intentionally empty */ }

}
