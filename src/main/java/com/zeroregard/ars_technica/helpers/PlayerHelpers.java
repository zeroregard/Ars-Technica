package com.zeroregard.ars_technica.helpers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PlayerHelpers {
    public static List<ServerPlayer> getNearbyPlayers(Vec3 position, Level world) {
        return getNearbyPlayers(position, world, 50);
    }

    public static List<ServerPlayer> getNearbyPlayers(Vec3 position, Level world, double radius) {
        double playerRadius = 50;
        AABB playerBounds = new AABB(position.subtract(playerRadius, playerRadius, playerRadius),
                position.add(playerRadius, playerRadius, playerRadius));
        return world.getEntitiesOfClass(ServerPlayer.class, playerBounds);
    }
}
