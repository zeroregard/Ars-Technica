package net.mcreator.ars_technica.setup;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.blocks.SourceEngineBlockEntity;
import net.mcreator.ars_technica.common.entity.ArcaneHammerEntity;
import net.mcreator.ars_technica.common.entity.ArcanePolishEntity;
import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ArsTechnicaMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArsTechnicaMod.MODID);

    static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () -> builder.build(ArsTechnicaMod.MODID + ":" + name));
    }

    static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(String name, BlockEntityType.Builder<T> builder) {
        return BLOCK_ENTITY_TYPES.register(name, () -> builder.build(null));
    }

    public static final RegistryObject<EntityType<WhirlEntity>> WHIRL_ENTITY = registerEntity(
            "whirl_entity",
            EntityType.Builder.<WhirlEntity>of(WhirlEntity::new, MobCategory.MISC).sized(1.0f, 1.0f));


    public static final RegistryObject<EntityType<ArcanePressEntity>> ARCANE_PRESS_ENTITY = registerEntity(
            "arcane_press_entity",
            EntityType.Builder.<ArcanePressEntity>of(ArcanePressEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));

    public static final RegistryObject<EntityType<ArcanePolishEntity>> ARCANE_POLISH_ENTITY = registerEntity(
            "arcane_polish_entity",
            EntityType.Builder.<ArcanePolishEntity>of(ArcanePolishEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));

    public static final RegistryObject<EntityType<ArcaneHammerEntity>> ARCANE_HAMMER_ENTITY = registerEntity(
            "arcane_hammer_entity",
            EntityType.Builder.<ArcaneHammerEntity>of(ArcaneHammerEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));

    public static final RegistryObject<BlockEntityType<SourceEngineBlockEntity>> SOURCE_ENGINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "source_engine_block_entity",
            () -> BlockEntityType.Builder.of(SourceEngineBlockEntity::new, BlockRegistry.SOURCE_ENGINE.get()).build(null)
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}