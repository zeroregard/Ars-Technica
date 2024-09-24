package net.mcreator.ars_technica.setup;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ArsTechnicaMod.MODID);

    public static final RegistryObject<EntityType<WhirlEntity>> WHIRL_ENTITY = ENTITY_TYPES.register("whirl_entity",
            () -> EntityType.Builder.<WhirlEntity>of(WhirlEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build("whirl_entity"));

    public static final RegistryObject<EntityType<ArcanePressEntity>> ARCANE_PRESS_ENTITY = ENTITY_TYPES.register("arcane_press_entity",
            () -> EntityType.Builder.<ArcanePressEntity>of(ArcanePressEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build("whirl_entity"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}