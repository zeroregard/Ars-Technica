package net.mcreator.ars_technica;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {

    public static class Common {

        public static ForgeConfigSpec.BooleanValue SCHEMATIC_CANNON_SPEED_BOOST_ENABLED;
        public static ForgeConfigSpec.ConfigValue<Double> SCHEMATIC_CANNON_SPEED_BOOST_RANGE;

        // public static ForgeConfigSpec.BooleanValue FOCUS_OF_TRANSMUTATION_RECIPE_ENABLED;



        public Common(ForgeConfigSpec.Builder builder) {

            builder.comment("Adjust these settings for Technomancer armor perks")
                    .push("Technomancer Armor");

            SCHEMATIC_CANNON_SPEED_BOOST_ENABLED = builder.comment("If enabled, schematic cannons will fire faster when players wearing the full Technomancer set are nearby schematic cannons")
                    .define("Schematic Cannon speed boost enabled", true);
            SCHEMATIC_CANNON_SPEED_BOOST_RANGE = builder.comment("Range for above-mentioned perk, if enabled.")
                    .define("Schematic Cannon speed boost range", 8D);
            builder.pop();

            // builder.comment("Adjust these settings for curios")
            //        .push("Curios");
            // FOCUS_OF_TRANSMUTATION_RECIPE_ENABLED = builder.comment("If enabled, it is possible to craft the Focus of Transmutation in the Enchanting Apparatus")
            //        .define("Focus of Transmutation", true);
            // builder.pop();

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