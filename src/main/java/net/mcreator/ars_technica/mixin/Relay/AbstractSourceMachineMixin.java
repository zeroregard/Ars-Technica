package net.mcreator.ars_technica.mixin.Relay;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.mcreator.ars_technica.common.helpers.mixins.IAverageTransferRateAccessor;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSourceMachine.class)
public abstract class AbstractSourceMachineMixin implements IAverageTransferRateAccessor {
    private final int[] transferRates = new int[30];
    private int currentIndex = 0;
    private int rateCount = 0;
    private int averageTransferRatePerSecond = 0;
    private long lastUpdateTime = 0;

    private static String AVERAGE_SOURCE_TRANSFER_PER_SECOND = "AverageTransferRatePerSecond";

    @Inject(method = "transferSource(Lcom/hollingsworth/arsnouveau/api/source/ISourceTile;Lcom/hollingsworth/arsnouveau/api/source/ISourceTile;)I", at = @At("HEAD"), remap = false)
    private void modifyTransferSource(ISourceTile from, ISourceTile to, CallbackInfoReturnable<Integer> cir) {
        AbstractSourceMachine entity = (AbstractSourceMachine) (Object) this;
        int transferRate = entity.getTransferRate(from, to);
        updateAverage(transferRate);
    }

    @Inject(method = "transferSource(Lcom/hollingsworth/arsnouveau/api/source/ISourceTile;Lcom/hollingsworth/arsnouveau/api/source/ISourceTile;I)I", at = @At("HEAD"), remap = false)
    private void modifyTransferSource(ISourceTile from, ISourceTile to, int transferRate, CallbackInfoReturnable<Integer> cir) {
        updateAverage(transferRate);
    }

    private void updateAverage(int transferRate) {
        AbstractSourceMachine entity = (AbstractSourceMachine) (Object) this;

        long currentTime = System.currentTimeMillis();
        int timeDifference = (int) (currentTime - lastUpdateTime);

        if (timeDifference > 1000) {
            transferRates[currentIndex] = transferRate;
            currentIndex = (currentIndex + 1) % transferRates.length;
            if (rateCount < transferRates.length) {
                rateCount++;
            }

            int total = 0;
            for (int i = 0; i < rateCount; i++) {
                total += transferRates[i];
            }

            averageTransferRatePerSecond = total / rateCount;

            lastUpdateTime = currentTime;
            entity.setChanged();
        }


    }

    @Inject(method="saveAdditional", at = @At("TAIL"), remap = false)
    private void modifySaveAdditional(CompoundTag tag, CallbackInfo cir) {
        tag.putInt(AVERAGE_SOURCE_TRANSFER_PER_SECOND, averageTransferRatePerSecond);
    }

    @Inject(method="load", at = @At("TAIL"), remap = false)
    private void modifyLoad(CompoundTag tag, CallbackInfo cir) {
        if (tag.contains(AVERAGE_SOURCE_TRANSFER_PER_SECOND)) {
            averageTransferRatePerSecond = tag.getInt(AVERAGE_SOURCE_TRANSFER_PER_SECOND);
        }
    }

    @Override
    public int getAverageTransferRatePerSecond() {
        return averageTransferRatePerSecond;
    }
}