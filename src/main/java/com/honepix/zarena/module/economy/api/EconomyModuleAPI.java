package com.honepix.zarena.module.economy.api;

import com.honepix.userapi.data.User;
import com.honepix.zarena.module.economy.data.UserEconomy;
import com.honepix.zarena.module.economy.data.UserEconomyService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EconomyModuleAPI {

    private final UserEconomyService economyService;

    private static EconomyModuleAPI INSTANCE;

    EconomyModuleAPI(@NotNull UserEconomyService economyService) {
        this.economyService = economyService;
    }

    public static EconomyModuleAPI getEconomyAPI() {
        return INSTANCE;
    }

    public static void init(UserEconomyService economyService) {
        INSTANCE = new EconomyModuleAPI(economyService);
    }

    public void createUserEconomy(UserEconomy userEconomy) {
        economyService.saveUserEconomy(userEconomy);
    }

    public void updateUserEconomy(UserEconomy userEconomy) {
        economyService.saveUserEconomy(userEconomy);
    }

    public Optional<UserEconomy> getUserEconomy(Player player) {
        return economyService.getUserEconomy(player);
    }

    public Optional<UserEconomy> getUserEconomy(User user) {
        return economyService.getUserEconomy(user);
    }

    public ItemStack createItemStack(int amount) {
        return economyService.createCoinItem(amount);
    }
}
