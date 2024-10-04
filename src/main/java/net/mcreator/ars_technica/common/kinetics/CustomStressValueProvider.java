package net.mcreator.ars_technica.common.kinetics;

import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class CustomStressValueProvider implements BlockStressValues.IStressValueProvider {

    private final double capacity;
    private final Couple<Integer> generatedRPM;

    public CustomStressValueProvider(double capacity, Couple<Integer> generatedRPM) {
        this.capacity = capacity;
        this.generatedRPM = generatedRPM;
    }

    @Override
    public double getImpact(Block block) {
        return 0;
    }

    @Override
    public double getCapacity(Block block) {
        return capacity;
    }

    @Override
    public boolean hasImpact(Block block) {
        return false;
    }

    @Override
    public boolean hasCapacity(Block block) {
        return true;
    }

    @Nullable
    @Override
    public Couple<Integer> getGeneratedRPM(Block block) {
        return generatedRPM;
    }
}