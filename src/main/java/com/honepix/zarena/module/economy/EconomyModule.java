package com.honepix.zarena.module.economy;

import com.honepix.lib.HonepixLib;
import com.honepix.userapi.UserAPI;
import com.honepix.userapi.provider.UserProvider;
import com.honepix.zarena.module.economy.api.EconomyModuleAPI;
import com.honepix.zarena.module.economy.command.AdminEconomyCommand;
import com.honepix.zarena.module.economy.command.InvalidUsageHandlerImpl;
import com.honepix.zarena.module.economy.command.PermissionHandlerImpl;
import com.honepix.zarena.module.economy.config.EconomyMessages;
import com.honepix.zarena.module.economy.data.UserEconomyService;
import com.honepix.zarena.module.economy.data.UserEconomyServiceImpl;
import com.j256.ormlite.jdbc.db.MariaDbDatabaseType;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

@Getter
public final class EconomyModule extends JavaPlugin {

    private DataSource dataSource;
    private final MariaDbDatabaseType databaseType = new MariaDbDatabaseType();
    private UserEconomyService economyService;
    private EconomyMessages economyMessages;
    private LiteCommands<CommandSender> liteCommands;
    private UserProvider userProvider;

    private static EconomyModule instance;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        userProvider = UserAPI.getInstance().getUserProvider();
        dataSource = HonepixLib.getInstance().getDataSource();
        economyMessages = new EconomyMessages(this);
        economyService = new UserEconomyServiceImpl(this);
        liteCommands = LitePaperAdventureFactory.builder(getServer(), "economy-module")
                .argument(Player.class, new BukkitPlayerArgument<>(getServer(), "&cИгрока нет на сервере"))

                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("Только игроки могут использовать эту команду"))

                //Commands
                .commandInstance(new AdminEconomyCommand(economyService, economyMessages))

                // Handlers
                .invalidUsageHandler(new InvalidUsageHandlerImpl(economyMessages))
                .permissionHandler(new PermissionHandlerImpl())
                .register();
        EconomyModuleAPI.init(economyService);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EconomyModule getInstance() {
        return instance;
    }
}
