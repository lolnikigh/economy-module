package com.honepix.zarena.module.economy.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class UserCoinReceiveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final long amount;
    private final Player player;

    public UserCoinReceiveEvent(long amount, @NotNull Player player) {
        this.amount = amount;
        this.player = player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
