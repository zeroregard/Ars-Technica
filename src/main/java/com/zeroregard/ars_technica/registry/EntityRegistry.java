package com.zeroregard.ars_technica.registry;


import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.block.SourceMotorBlockEntity;
import com.zeroregard.ars_technica.entity.ArcaneHammerEntity;
import com.zeroregard.ars_technica.entity.ArcanePolishEntity;
import com.zeroregard.ars_technica.entity.ArcanePressEntity;
import com.zeroregard.ars_technica.entity.ArcaneWhirlEntity;
import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ArsTechnica.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ArsTechnica.MODID);


    static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(ArsTechnica.MODID + ":" + name));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<ArcanePolishEntity>> ARCANE_POLISH_ENTITY = registerEntity(
            "arcane_polish_entity",
            EntityType.Builder.<ArcanePolishEntity>of(ArcanePolishEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneHammerEntity>> ARCANE_HAMMER_ENTITY = registerEntity(
            "arcane_hammer_entity",
            EntityType.Builder.<ArcaneHammerEntity>of(ArcaneHammerEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcanePressEntity>> ARCANE_PRESS_ENTITY = registerEntity(
            "arcane_press_entity",
            EntityType.Builder.<ArcanePressEntity>of(ArcanePressEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneFusionEntity>> ARCANE_FUSION_ENTITY = registerEntity(
            "arcane_fusion_entity",
            EntityType.Builder.<ArcaneFusionEntity>of(ArcaneFusionEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneWhirlEntity>> ARCANE_WHIRL_ENTITY = registerEntity(
            "arcane_whirl_entity",
            EntityType.Builder.<ArcaneWhirlEntity>of(ArcaneWhirlEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F));


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SourceMotorBlockEntity>> SOURCE_MOTOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("source_motor_block_entity", () -> {
                return BlockEntityType.Builder.of(SourceMotorBlockEntity::new, BlockRegistry.SOURCE_MOTOR.get())
                        .build(null);
            });
}
