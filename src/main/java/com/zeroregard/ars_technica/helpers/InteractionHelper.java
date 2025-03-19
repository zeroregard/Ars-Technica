package com.zeroregard.ars_technica.helpers;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;

public class InteractionHelper {
    public static ItemInteractionResult fromInteractionResult(InteractionResult result) {
        switch (result) {
            case SUCCESS:
            case SUCCESS_NO_ITEM_USED:
                return ItemInteractionResult.SUCCESS;
            case CONSUME:
                return ItemInteractionResult.CONSUME;
            case CONSUME_PARTIAL:
                return ItemInteractionResult.CONSUME_PARTIAL;
            case PASS:
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case FAIL:
                return ItemInteractionResult.FAIL;
            default:
                throw new IllegalArgumentException("Unhandled InteractionResult: " + result);
        }
    }

}
