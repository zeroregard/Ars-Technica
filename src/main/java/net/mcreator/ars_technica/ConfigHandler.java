package net.mcreator.ars_technica;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {

    public static class Common {

        public static ForgeConfigSpec.IntValue ARMOR_MAX_MANA;
        public static ForgeConfigSpec.IntValue ARMOR_MANA_REGEN;
        public static ForgeConfigSpec.BooleanValue SCHEMATIC_CANNON_SPEED_BOOST_ENABLED;
        public static ForgeConfigSpec.ConfigValue<Double> SCHEMATIC_CANNON_SPEED_BOOST_RANGE;

        public static ForgeConfigSpec.ConfigValue<Double> SOURCE_MOTOR_SPEED_TO_SOURCE_MULTIPLIER;

        // Recipes
        public final ForgeConfigSpec.BooleanValue RECIPE_FOCUS_TRANSMUTATION_ENABLED;
        public static final String RECIPE_FOCUS_TRANSMUTATION_CONFIG_OPTION = "recipeFocusTransmutationEnabled";

        public final ForgeConfigSpec.BooleanValue RECIPE_CALIBRATED_PRECISION_MECHANISM_ENABLED;
        public static final String RECIPE_CALIBRATED_PRECISION_MECHANISM_CONFIG_OPTION = "recipeCalibratedPrecisionMechanismEnabled";

        public final ForgeConfigSpec.BooleanValue RECIPE_RUNIC_SPANNER_ENABLED;
        public static final String RECIPE_RUNIC_SPANNER_CONFIG_OPTION = "recipeRunicSpannerEnabled";

        public final ForgeConfigSpec.BooleanValue RECIPE_THREAD_PRESSURE_ENABLED;
        public static final String RECIPE_THREAD_PRESSURE_CONFIG_OPTION = "recipeThreadPressureEnabled";

        public final ForgeConfigSpec.BooleanValue RECIPE_SPY_MONOCLE_ENABLED;
        public static final String RECIPE_SPY_MONOCLE_CONFIG_OPTION = "recipeSpyMonocleEnabled";



        public Common(ForgeConfigSpec.Builder builder) {

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
            SOURCE_MOTOR_SPEED_TO_SOURCE_MULTIPLIER = builder.comment("Determines the multiplier used in the cost for generating rotation, defaults to 7.5")
                    .defineInRange("sourceMotorSpeedToSourceMultiplier", 7.5f, 0f, 100f);
            builder.pop();

            builder.comment("Set these variables to false/true to disable/enable recipes")
                    .push("Recipes");

            RECIPE_FOCUS_TRANSMUTATION_ENABLED = builder
                    .define(RECIPE_FOCUS_TRANSMUTATION_CONFIG_OPTION, true);

            RECIPE_CALIBRATED_PRECISION_MECHANISM_ENABLED = builder
                    .define(RECIPE_CALIBRATED_PRECISION_MECHANISM_CONFIG_OPTION, true);

            RECIPE_RUNIC_SPANNER_ENABLED = builder
                    .define(RECIPE_RUNIC_SPANNER_CONFIG_OPTION, true);

            RECIPE_THREAD_PRESSURE_ENABLED = builder
                    .define(RECIPE_THREAD_PRESSURE_CONFIG_OPTION, true);

            RECIPE_SPY_MONOCLE_ENABLED = builder
                    .define(RECIPE_SPY_MONOCLE_CONFIG_OPTION, true);

            builder.pop();

        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;


    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

    }

}