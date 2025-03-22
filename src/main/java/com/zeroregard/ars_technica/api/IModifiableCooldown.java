package com.zeroregard.ars_technica.api;

public interface IModifiableCooldown {
    void setCooldownTicks(int ticks);
    int getCooldownTicks();
}
