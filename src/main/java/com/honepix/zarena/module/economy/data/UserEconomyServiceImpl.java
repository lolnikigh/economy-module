package com.honepix.zarena.module.economy.data;

import com.honepix.zarena.module.economy.EconomyModule;
import com.honepix.zarena.module.economy.config.EconomyConfig;
import com.honepix.zarena.module.economy.config.EconomyItemModel;
import com.honepix.zarena.module.economy.api.event.UserCoinReceiveEvent;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class UserEconomyService implements Listener {

    private final EconomyModule module;
    private final Logger logger;

    private final Map<UUID, UserEconomy> CACHE = new HashMap<>();

    private UserEconomyDao userEconomyDao;
    private EconomyConfig economyConfig;

    public UserEconomyService(EconomyModule module) {
        this.module = module;
        this.logger = module.getLogger();

        try (ConnectionSource connectionSource = new DataSourceConnectionSource(module.getDataSource(), module.getDatabaseType())) {
            this.userEconomyDao = new UserEconomyDaoImpl(connectionSource);
            TableUtils.createTableIfNotExists(connectionSource, UserEconomy.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.economyConfig = new EconomyConfig(module);
        Bukkit.getPluginManager().registerEvents(this, module);
    }

    public Optional<UserEconomy> getUserEconomy(String playerName) {
        try {
            return userEconomyDao.findByName(playerName)
                    .stream().findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(Player player) {
        try {
            return userEconomyDao.idExists(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(UserEconomy userEconomy) {
        try {
            return userEconomyDao.update(userEconomy);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUserEconomy(Player player) {
        try {
            userEconomyDao.create(new UserEconomy(player));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserEconomy> getUserEconomy(Player player) {
        UserEconomy userEconomy;
        try {
            userEconomy = userEconomyDao.queryForId(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (userEconomy == null) return Optional.empty();
        return Optional.of(userEconomy);
    }

    public ItemStack createCoinItem(int amount) {
        return economyConfig.createCoinItem(amount);
    }

    @EventHandler
    private void onUserCoinReceive(UserCoinReceiveEvent event) throws SQLException {
        Player player = event.getPlayer();
        long receivedCoins = event.getAmount();
        UUID id = player.getUniqueId();
        Optional<UserEconomy> optionalUserEconomy = getUserEconomy(player);
        if (optionalUserEconomy.isEmpty()) {
            return;
        }
        UserEconomy userEconomy = optionalUserEconomy.get();
        userEconomy.addCoins(receivedCoins);
        Component message = economyConfig.getFormatMessage()
                .replaceText(builder -> builder
                        .matchLiteral("$coins")
                        .replacement(String.valueOf(receivedCoins)));
        player.sendMessage(message);
        userEconomyDao.update(userEconomy);
        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.PLAYER, 1, 1));

    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        UserEconomy economy = userEconomyDao.createIfNotExists(new UserEconomy(id, player.getName()));
        System.out.println("created " + economy);
    }

    @EventHandler
    private void onPlayerPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        Item item = event.getItem();
        if (entity instanceof Player player) {
            ItemStack itemStack = item.getItemStack();
            EconomyItemModel itemModel = economyConfig.getItemModel();
            if (itemModel.isCoinItem(itemStack)) {
                event.setCancelled(true);
                int itemAmount = itemStack.getAmount();
                int coinAmount = itemAmount * economyConfig.getExchangeRate();
                double modifier = economyConfig.getCoinModifier(player);
                long summaryCoins = Math.round(coinAmount * modifier);
                item.remove();
                Bukkit.getPluginManager().callEvent(new UserCoinReceiveEvent(summaryCoins, player));
            }
        }
    }

}
