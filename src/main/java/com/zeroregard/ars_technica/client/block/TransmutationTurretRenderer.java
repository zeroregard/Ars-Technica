package com.zeroregard.ars_technica.client.block;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zeroregard.ars_technica.block.TransmutationTurretTile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TransmutationTurretRenderer implements BlockEntityRenderer<TransmutationTurretTile> {

    public TransmutationTurretRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TransmutationTurretTile tile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        Level level = tile.getLevel();
        if (level == null) return;
        
        BlockPos pos = tile.getBlockPos();
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        // Add transmutation-themed particle effects
        if (level.getGameTime() % 20 == 0) { // Spawn particles every second
            // Create floating particles around the turret
            double angle = (level.getGameTime() * 0.1) % (2 * Math.PI);
            double radius = 0.8;
            double particleX = x + Math.sin(angle) * radius;
            double particleZ = z + Math.cos(angle) * radius;
            double particleY = y + 0.2 + 0.1 * Math.sin(level.getGameTime() * 0.2);
            
            // Create target position for the particle
            BlockPos targetPos = new BlockPos((int)particleX, (int)particleY, (int)particleZ);
            
            // Spawn transmutation-colored particles (purple/magenta theme)
            ParticleColor purpleColor = new ParticleColor(0.8f, 0.2f, 0.8f);
            ParticleUtil.spawnFollowProjectile(level, pos, targetPos, purpleColor);
            
            // Add a second particle for more visual effect
            double angle2 = angle + Math.PI;
            double particleX2 = x + Math.sin(angle2) * radius;
            double particleZ2 = z + Math.cos(angle2) * radius;
            BlockPos targetPos2 = new BlockPos((int)particleX2, (int)particleY, (int)particleZ2);
            
            ParticleColor magentaColor = new ParticleColor(0.6f, 0.1f, 0.8f);
            ParticleUtil.spawnFollowProjectile(level, pos, targetPos2, magentaColor);
        }
        
        // Add occasional sparkle effects
        if (level.getGameTime() % 40 == 0) { // Every 2 seconds
            double sparkleX = x + (Math.random() - 0.5) * 0.5;
            double sparkleY = y + Math.random() * 0.5;
            double sparkleZ = z + (Math.random() - 0.5) * 0.5;
            BlockPos sparklePos = new BlockPos((int)sparkleX, (int)sparkleY, (int)sparkleZ);
            
            ParticleColor sparkleColor = new ParticleColor(1.0f, 0.5f, 1.0f);
            ParticleUtil.spawnFollowProjectile(level, pos, sparklePos, sparkleColor);
        }
    }
}