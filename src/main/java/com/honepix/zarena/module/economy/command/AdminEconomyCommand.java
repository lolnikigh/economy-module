package com.honepix.zarena.module.economy.command;


import com.honepix.zarena.module.economy.config.EconomyMessages;
import com.honepix.zarena.module.economy.data.UserEconomy;
import com.honepix.zarena.module.economy.data.UserEconomyService;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Optional;

@Route(name = "economy", aliases = "eco")
@Permission("zarena.economy.admin")
@AllArgsConstructor
public class AdminEconomyCommand {

    private UserEconomyService economyService;
    private EconomyMessages economyMessages;

    @Execute(route = "add")
    public void add(Player sender, @Arg String recipientName, @Arg int amount) {
        Optional<UserEconomy> optionalUserEconomy = economyService.getUserEconomyByUsername(recipientName);
        if (optionalUserEconomy.isEmpty()) {
            sender.sendMessage("Игрока нет в базе");
        } else {
            UserEconomy userEconomy = optionalUserEconomy.get();
            userEconomy.addCoins(amount);
            economyService.saveUserEconomy(userEconomy);
            sender.sendMessage(economyMessages.commandCoinsGived(recipientName, amount));
        }
    }

    @Execute(route = "set")
    public void set(Player sender, @Arg String recipientName, @Arg int amount) {
        Optional<UserEconomy> optionalUserEconomy = economyService.getUserEconomyByUsername(recipientName);
        if (optionalUserEconomy.isEmpty()) {
            sender.sendMessage("игрока нет в базе");
        } else {
            UserEconomy userEconomy = optionalUserEconomy.get();
            userEconomy.setCoins(amount);
            economyService.saveUserEconomy(userEconomy);
            sender.sendMessage(economyMessages.commandCoinsGived(recipientName, amount));
        }
    }

    @Execute(route = "info")
    public void info(Player sender, @Arg String playerName) {
        Optional<UserEconomy> optionalUserEconomy = economyService.getUserEconomyByUsername(playerName);
        if (optionalUserEconomy.isEmpty()) {
            sender.sendMessage("игрока нет в базе");
        } else {
            UserEconomy userEconomy = optionalUserEconomy.get();
            sender.sendMessage(economyMessages.playerInfo(userEconomy));
        }
    }


}
