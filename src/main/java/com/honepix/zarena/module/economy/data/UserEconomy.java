package com.honepix.zarena.module.economy.data;

import com.honepix.userapi.data.User;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DatabaseTable(tableName = "user_economy")
@NoArgsConstructor
@Getter
public class UserEconomy {

    @DatabaseField(generatedId = true)
    private Integer economyId;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private User user;
    @DatabaseField
    private long coins;

    public UserEconomy(User user, long coins) {
        this.economyId = 1;
        this.user = user;
        this.coins = coins;
    }

    public UserEconomy(User user) {
        this(user, 0);
    }

    public void addCoins(long amount) {
        coins += amount;
    }

    public void setCoins(long amount) {
        this.coins = amount;
    }

    public boolean hasMoreThan(long amount) {
        return coins >= amount;
    }

    public boolean subtractCoins(long amount) {
        if (hasMoreThan(amount)) {
            coins -= amount;
            return true;
        }
        return false;
    }

}
