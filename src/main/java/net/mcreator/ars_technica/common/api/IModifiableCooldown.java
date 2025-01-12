package net.mcreator.ars_technica.common.api;

public interface IModifiableCooldown {
    void setCooldownTicks(int ticks);
    int getCooldownTicks();
}
