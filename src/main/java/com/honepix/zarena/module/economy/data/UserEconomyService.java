package com.honepix.zarena.module.economy.data;

import com.honepix.userapi.data.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface UserEconomyService {

    ItemStack createCoinItem(int amount);

    Optional<UserEconomy> getUserEconomyByUsername(String username);

    Optional<UserEconomy> getUserEconomy(User user);

    Optional<UserEconomy> getUserEconomy(Player player);

    void saveUserEconomy(UserEconomy userEconomy);
}
