package net.mcreator.ars_technica.datagen;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConditionalApparatusRecipeBuilder extends ApparatusRecipeBuilder {
    private List<JsonObject> conditions = new ArrayList<>();

    public ConditionalApparatusRecipeBuilder() {
        super();
    }

    public static ConditionalApparatusRecipeBuilder conditionalBuilder() {
        return new ConditionalApparatusRecipeBuilder();
    }

    private ConditionalApparatusRecipeBuilder withCondition(JsonObject condition) {
        this.conditions.add(condition);
        return this;
    }

    public ConditionalApparatusRecipeBuilder withConfigCondition(String configOption) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "ars_technica:config_enabled");
        condition.addProperty("config_option", configOption);
        condition.addProperty("value", true);
        return this.withCondition(condition);
    }

    @Override
    public EnchantingApparatusRecipeWithConditions build() {
        EnchantingApparatusRecipe baseRecipe = super.build();
        return new EnchantingApparatusRecipeWithConditions(baseRecipe, this.conditions);
    }

}