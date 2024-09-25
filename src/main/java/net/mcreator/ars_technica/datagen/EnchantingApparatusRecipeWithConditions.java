package net.mcreator.ars_technica.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import java.util.List;

public class EnchantingApparatusRecipeWithConditions extends EnchantingApparatusRecipe {
    public List<JsonObject> conditions;

    public EnchantingApparatusRecipeWithConditions(EnchantingApparatusRecipe baseRecipe, List<JsonObject> conditions) {
        super(baseRecipe.getId(), baseRecipe.pedestalItems, baseRecipe.reagent, baseRecipe.result, baseRecipe.sourceCost, baseRecipe.keepNbtOfReagent);
        this.conditions = conditions;
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject json = (JsonObject) super.asRecipe();
        JsonArray conditionArray = new JsonArray();
        for (JsonObject condition : conditions) {
            conditionArray.add(condition);
        }
        json.add("conditions", conditionArray);
        return json;
    }
}