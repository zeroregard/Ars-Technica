package net.mcreator.ars_technica.recipe;

import com.google.gson.JsonObject;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.ConfigHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigRecipeCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation(ArsTechnicaMod.MODID, "config_enabled");
    private final String configKey;

    public ConfigRecipeCondition(String configKey) {
        this.configKey = configKey;
    }

    @Override
    public boolean test(IContext context) {
        switch (configKey) {
            case ConfigHandler.Common.RECIPE_FOCUS_TRANSMUTATION_CONFIG_OPTION:
                return ConfigHandler.COMMON.RECIPE_FOCUS_TRANSMUTATION_ENABLED.get();
            case ConfigHandler.Common.RECIPE_CALIBRATED_PRECISION_MECHANISM_CONFIG_OPTION:
                return ConfigHandler.COMMON.RECIPE_CALIBRATED_PRECISION_MECHANISM_ENABLED.get();
            case ConfigHandler.Common.RECIPE_RUNIC_SPANNER_CONFIG_OPTION:
                return ConfigHandler.COMMON.RECIPE_RUNIC_SPANNER_ENABLED.get();
            case ConfigHandler.Common.RECIPE_THREAD_PRESSURE_CONFIG_OPTION:
                return ConfigHandler.COMMON.RECIPE_THREAD_PRESSURE_ENABLED.get();
            case ConfigHandler.Common.RECIPE_SPY_MONOCLE_CONFIG_OPTION:
                return ConfigHandler.COMMON.RECIPE_SPY_MONOCLE_ENABLED.get();
            default:
                return true;
        }
    }


    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    public static class Serializer implements IConditionSerializer<ConfigRecipeCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ConfigRecipeCondition condition) {
            json.addProperty("config_option", condition.configKey);
        }

        @Override
        public ConfigRecipeCondition read(JsonObject json) {
            return new ConfigRecipeCondition(json.get("config_option").getAsString());
        }

        @Override
        public ResourceLocation getID() {
            return ConfigRecipeCondition.NAME;
        }
    }
}