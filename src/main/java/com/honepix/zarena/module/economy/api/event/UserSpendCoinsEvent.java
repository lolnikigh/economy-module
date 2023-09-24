package com.honepix.zarena.module.economy.api.event;

import com.honepix.userapi.data.User;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class UserSpendCoinsEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final long amount;
    private final User user;
    private final Component successMessage;
    private boolean success;

    public UserSpendCoinsEvent(long amount, @NotNull User user, @NotNull Component successMessage) {
        this.amount = amount;
        this.user = user;
        this.successMessage = successMessage;
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
