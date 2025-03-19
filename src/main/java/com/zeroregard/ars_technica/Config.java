package com.zeroregard.ars_technica;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static class Common {

        public static ModConfigSpec.IntValue ARMOR_MAX_MANA;
        public static ModConfigSpec.IntValue ARMOR_MANA_REGEN;

        public static ModConfigSpec.BooleanValue FLUID_CAN_BE_PLACED;
        public static ModConfigSpec.BooleanValue FLUID_SOURCES_CAN_BE_PLACED;
        public static ModConfigSpec.IntValue FLUID_MAX_PLACEMENTS_PER_FUSE;

        public static ModConfigSpec.ConfigValue<Double> SOURCE_MOTOR_SPEED_TO_SOURCE_MULTIPLIER;

        public static ModConfigSpec.BooleanValue SCHEMATIC_CANNON_SPEED_BOOST_ENABLED;
        public static ModConfigSpec.ConfigValue<Double> SCHEMATIC_CANNON_SPEED_BOOST_RANGE;


        public static ModConfigSpec.IntValue RUNE_MIN_COOLDOWN_VALUE;
        public static ModConfigSpec.IntValue RUNE_MAX_COOLDOWN_VALUE;

        public Common(ModConfigSpec.Builder builder) {

            builder.comment("Adjust these variables for Technomancer armor settings")
                    .push("Technomancer");

            ARMOR_MAX_MANA = builder.comment("Max mana bonus for each armor piece").defineInRange("armorMaxMana", 100, 0, 10000);
            ARMOR_MANA_REGEN = builder.comment("Mana regen bonus for each armor piece").defineInRange("armorManaRegen", 4, 0, 100);
            SCHEMATIC_CANNON_SPEED_BOOST_ENABLED = builder.comment("If enabled, schematic cannons will fire faster when players wearing the full Technomancer set are nearby schematic cannons")
                    .define("schematicCannonSpeedBoostEnabled", true);
            SCHEMATIC_CANNON_SPEED_BOOST_RANGE = builder.comment("Range for above-mentioned perk, if enabled.")
                    .define("schematicCannonSpeedBoostRange", 8D);

            builder.pop();

            builder.comment("Adjust these variables for block settings")
                    .push("Blocks");
            SOURCE_MOTOR_SPEED_TO_SOURCE_MULTIPLIER = builder.comment("Determines the multiplier used in the cost for generating rotation, defaults to 4.0")
                    .defineInRange("sourceMotorSpeedToSourceMultiplier", 4f, 0f, 100f);
            builder.pop();

            builder.comment("Logic related to Fuse")
                    .push("Glyph - Fuse");

            FLUID_CAN_BE_PLACED = builder.define("fluidCanBePlaced", true);
            FLUID_SOURCES_CAN_BE_PLACED = builder.define("fluidSourcesCanBePlaced", true);
            FLUID_MAX_PLACEMENTS_PER_FUSE = builder.defineInRange("fluidMaxPlacementsPerFuse", 16, 1, 256);

            builder.pop();

            builder.comment("Entity tweakable (Wrench) cooldown settings for source relays and spell runes")
                    .push("Entity cooldown");

            RUNE_MIN_COOLDOWN_VALUE = builder.defineInRange("runeMinCooldown", 5, 0, 40);
            RUNE_MAX_COOLDOWN_VALUE = builder.defineInRange("runeMaxCooldown", 600, 40, 6000);

            builder.pop();

        }
    }

    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    public static class Client {

        public Client(ModConfigSpec.Builder builder) {

        }
    }

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {

        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        final Pair<Client, ModConfigSpec> specClientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specClientPair.getRight();
        CLIENT = specClientPair.getLeft();

    }

}