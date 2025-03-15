package com.zeroregard.ars_technica.registry;


import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.entity.ArcaneHammerEntity;
import com.zeroregard.ars_technica.entity.ArcanePolishEntity;
import com.zeroregard.ars_technica.entity.ArcanePressEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ArsTechnica.MODID);


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

}
