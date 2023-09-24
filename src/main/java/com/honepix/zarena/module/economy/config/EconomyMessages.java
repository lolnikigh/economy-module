package com.honepix.zarena.module.economy.config;

import com.honepix.lib.util.ComponentUtils;
import com.honepix.userapi.data.User;
import com.honepix.zarena.module.economy.CoinDeclension;
import com.honepix.zarena.module.economy.EconomyModule;
import com.honepix.zarena.module.economy.data.UserEconomy;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.Configuration;

import static com.honepix.lib.util.ComponentUtils.fromLegacy;

public class EconomyMessages {

    private final CoinDeclension coinDeclension = new CoinDeclension();

    private final Component formatCommandReceivedCoins;
    private final Component formatCommandCoinsGived;
    private final Component formatCommandSetCoins;
    private final Component formatCommandPlayerInfo;
    private final Component formatNotEnough;

    public EconomyMessages(EconomyModule module) {
        Configuration configuration = module.getConfig();
        formatCommandReceivedCoins = fromLegacy(configuration.getString("message.command-received-coins"));
        formatCommandCoinsGived = fromLegacy(configuration.getString("message.command-coins-gived"));
        formatCommandSetCoins = fromLegacy(configuration.getString("message.command-set-coins"));
        formatCommandPlayerInfo = fromLegacy(configuration.getString("message.info"));
        formatNotEnough = fromLegacy(configuration.getString("message.not-enough-coins"));
    }

    public Component notEnoughCoins(long coins) {
        return formatNotEnough
                .replaceText(builder -> builder
                        .matchLiteral("$coins")
                        .replacement(Component.text(declension(coins))));
    }

    public Component playerInfo(UserEconomy economy) {
        User user = economy.getUser();
        return formatCommandPlayerInfo
                .replaceText(builder -> builder
                        .matchLiteral("$coins")
                        .replacement(Component.text(declension(economy.getCoins()))))
                .replaceText(builder -> builder
                        .matchLiteral("$player")
                        .replacement(Component.text(user.getName())));
    }

    public Component commandSetCoins(String playerName, int amount) {
        return formatCommandSetCoins
                .replaceText(builder -> builder
                        .matchLiteral("$coins")
                        .replacement(Component.text(declension(amount))))
                .replaceText(builder -> builder
                        .matchLiteral("$player")
                        .replacement(Component.text(playerName)));
    }

    public Component commandCoinsGived(String playerName, int amount) {
        return formatCommandCoinsGived
                .replaceText(builder -> builder
                        .matchLiteral("$coins")
                        .replacement(Component.text(declension(amount))))
                .replaceText(builder -> builder
                        .matchLiteral("$player")
                        .replacement(Component.text(playerName)));
    }

    public Component commandReceivedCoins(long amount) {
        return formatCommandReceivedCoins.replaceText(builder -> builder
                .matchLiteral("$coins")
                .replacement(Component.text(declension(amount))));
    }

    private String declension(long coins) {
        return ComponentUtils.declension(coins, coinDeclension);
    }
}
