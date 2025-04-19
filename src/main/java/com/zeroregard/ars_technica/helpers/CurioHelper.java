package com.zeroregard.ars_technica.helpers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public class CurioHelper {
    public static boolean hasTaggedCurio(ServerPlayer player, ResourceLocation tagId) {
        TagKey<Item> tagKey = TagKey.create(net.minecraft.core.registries.Registries.ITEM, tagId);

        return CuriosApi.getCuriosInventory(player).map(handler ->
                !handler.findCurios(stack -> stack.is(tagKey)).isEmpty()
        ).orElse(false);
    }

}
