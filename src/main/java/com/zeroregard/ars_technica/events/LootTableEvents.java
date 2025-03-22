package com.zeroregard.ars_technica.events;

import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import static net.minecraft.world.level.storage.loot.BuiltInLootTables.SIMPLE_DUNGEON;

@EventBusSubscriber(modid = ArsTechnica.MODID)
public class LootTableEvents {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(SIMPLE_DUNGEON)) {
            LootPool pool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(ItemRegistry.POCKET_FACTORY.get())
                            .setWeight(1))
                            .when(LootItemRandomChanceCondition.randomChance(0.5f))
                    .build();
            event.getTable().addPool(pool);
        }
    }
}