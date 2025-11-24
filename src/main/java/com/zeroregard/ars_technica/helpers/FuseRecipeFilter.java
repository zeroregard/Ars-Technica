package com.zeroregard.ars_technica.helpers;

import com.zeroregard.ars_technica.Config;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class FuseRecipeFilter {
    private FuseRecipeFilter() {
    }

    public static boolean isFiltered(ResourceLocation id) {
        if (id == null) {
            return false;
        }
        return Config.Common.FUSE_RECIPE_FILTER.get().stream()
                .map(Object::toString)
                .map(ResourceLocation::tryParse)
                .filter(Objects::nonNull)
                .anyMatch(id::equals);
    }
}
