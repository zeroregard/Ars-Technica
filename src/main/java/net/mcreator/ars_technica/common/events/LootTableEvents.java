package net.mcreator.ars_technica.common.events;

import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.level.storage.loot.BuiltInLootTables.SIMPLE_DUNGEON;

@Mod.EventBusSubscriber(modid = "ars_technica")
public class LootTableEvents {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(SIMPLE_DUNGEON)) {
            LootPool pool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(ItemsRegistry.POCKET_FACTORY_DISC.get())
                            .setWeight(1))
                            .when(LootItemRandomChanceCondition.randomChance(0.5f))
                    .build();
            event.getTable().addPool(pool);
        }
    }
}