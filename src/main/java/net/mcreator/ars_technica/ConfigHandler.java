package net.mcreator.ars_technica;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {

    public static class Common {

        public static ForgeConfigSpec.BooleanValue SCHEMATIC_CANNON_SPEED_BOOST_ENABLED;
        public static ForgeConfigSpec.ConfigValue<Double> SCHEMATIC_CANNON_SPEED_BOOST_RANGE;

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

        //public final ForgeConfigSpec.BooleanValue RECIPE_TECHNOMANCER_BOOTS_ENABLED;
        //public static final String RECIPE_TECHNOMANCER_BOOTS_CONFIG_OPTION = "recipeTechnomancerBootsEnabled";

        //public final ForgeConfigSpec.BooleanValue RECIPE_TECHNOMANCER_CHESTPLATE_ENABLED;
        //public static final String RECIPE_TECHNOMANCER_CHESTPLATE_CONFIG_OPTION = "recipeTechnomancerChestplateEnabled";

        //public final ForgeConfigSpec.BooleanValue RECIPE_TECHNOMANCER_LEGGINGS_ENABLED;
        //public static final String RECIPE_TECHNOMANCER_LEGGINGS_CONFIG_OPTION = "recipeTechnomancerLeggingsEnabled";

        //public final ForgeConfigSpec.BooleanValue RECIPE_TECHNOMANCER_HELMET_ENABLED;
        //public static final String RECIPE_TECHNOMANCER_HELMET_CONFIG_OPTION = "recipeTechnomancerHelmetEnabled";

        public Common(ForgeConfigSpec.Builder builder) {

            builder.comment("Adjust these variables for perk-related settings")
                    .push("Perks");

            SCHEMATIC_CANNON_SPEED_BOOST_ENABLED = builder.comment("If enabled, schematic cannons will fire faster when players wearing the full Technomancer set are nearby schematic cannons")
                    .define("schematicCannonSpeedBoostEnabled", true);
            SCHEMATIC_CANNON_SPEED_BOOST_RANGE = builder.comment("Range for above-mentioned perk, if enabled.")
                    .define("schematicCannonSpeedBoostRange", 8D);

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