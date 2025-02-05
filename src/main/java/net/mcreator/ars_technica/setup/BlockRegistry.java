package net.mcreator.ars_technica.setup;

import com.simibubi.create.AllBlocks;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.blocks.PreciseRelay;
import net.mcreator.ars_technica.common.blocks.turrets.EncasedTurretBlock;
import net.mcreator.ars_technica.common.blocks.SourceEngineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsTechnicaMod.MODID);

    public static final RegistryObject<SourceEngineBlock> SOURCE_ENGINE =
            BLOCKS.register("source_engine", () -> new SourceEngineBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .forceSolidOn()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.STONE)));

    // TODO: deprecated, remove
    public static final RegistryObject<EncasedTurretBlock> ANDESITE_ENCASED_TURRET_BLOCK =
            BLOCKS.register("encased_turret_block", () -> new EncasedTurretBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.PODZOL)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.WOOD), AllBlocks.ANDESITE_CASING::get));

    public static final RegistryObject<PreciseRelay> PRECISE_RELAY =
            BLOCKS.register("precise_relay", () -> new PreciseRelay(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .forceSolidOn()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.STONE)));


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
