package com.honepix.zarena.module.economy.data;

import com.honepix.userapi.data.User;
import com.honepix.userapi.event.UserJoinEvent;
import com.honepix.userapi.provider.UserProvider;
import com.honepix.zarena.module.economy.EconomyModule;
import com.honepix.zarena.module.economy.api.event.UserReceiveCoinsEvent;
import com.honepix.zarena.module.economy.api.event.UserSpendCoinsEvent;
import com.honepix.zarena.module.economy.config.EconomyConfig;
import com.honepix.zarena.module.economy.config.EconomyItemModel;
import com.honepix.zarena.module.economy.config.EconomyMessages;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

public class UserEconomyServiceImpl implements Listener, UserEconomyService {

    private final EconomyModule module;
    private final Logger logger;

    private final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    private final UserEconomyDao userEconomies;
    private final UserProvider userProvider;
    private final EconomyConfig economyConfig;
    private final EconomyMessages economyMessages;

    public UserEconomyServiceImpl(EconomyModule module) {
        this.module = module;
        this.logger = module.getLogger();

        try (ConnectionSource connectionSource = new DataSourceConnectionSource(module.getDataSource(), module.getDatabaseType())) {
            this.userEconomies = new UserEconomyDaoImpl(connectionSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.userProvider = module.getUserProvider();
        this.economyConfig = new EconomyConfig(module);
        this.economyMessages = module.getEconomyMessages();
        Bukkit.getPluginManager().registerEvents(this, module);
        startCacheCleaner();
    }

    @Override
    public void saveUserEconomy(UserEconomy userEconomy) {
        userEconomies.save(userEconomy);
    }

    public Optional<UserEconomy> getUserEconomy(Player player) {
        Optional<User> optionalUser = userProvider.getUser(player.getUniqueId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return userEconomies.findByUser(user);
        } else {
            return Optional.empty();
        }
    }

    public ItemStack createCoinItem(int amount) {
        return economyConfig.createCoinItem(amount);
    }

    @Override
    public Optional<UserEconomy> getUserEconomyByUsername(String username) {
        Optional<User> optionalUser = userProvider.getUser(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return getUserEconomy(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserEconomy> getUserEconomy(User user) {
        return userEconomies.findByUser(user);
    }

    @EventHandler
    private void onUserSpendCoins(UserSpendCoinsEvent event) {
        User user = event.getUser();
        Player player = Bukkit.getPlayer(user.getId());
        long spendCoins = event.getAmount();
        Optional<UserEconomy> optionalUserEconomy = getUserEconomy(user);
        if (optionalUserEconomy.isEmpty()) return;
        UserEconomy userEconomy = optionalUserEconomy.get();
        boolean subtracted = userEconomy.subtractCoins(spendCoins);
        if (!subtracted) {
            spendCoins -= userEconomy.getCoins();
            if (player != null) {
                player.sendMessage(economyMessages.notEnoughCoins(spendCoins));
            }
            event.setSuccess(false);
        } else {
            if (player != null) {
                player.sendMessage(event.getSuccessMessage());
            }
            event.setSuccess(true);
        }
    }

    @EventHandler
    private void onUserCoinReceive(UserReceiveCoinsEvent event) throws SQLException {
        Player player = event.getPlayer();
        long receivedCoins = event.getAmount();
        Optional<UserEconomy> optionalUserEconomy = getUserEconomy(player);
        if (optionalUserEconomy.isEmpty()) return;
        UserEconomy userEconomy = optionalUserEconomy.get();
        userEconomy.addCoins(receivedCoins);
        Component message = economyConfig.getFormatMessage()
                .replaceText(builder -> builder
                        .matchLiteral("$coins")
                        .replacement(String.valueOf(receivedCoins)));
        player.sendMessage(message);
        userEconomies.update(userEconomy);
        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.PLAYER, 1, 1));

    }

    @EventHandler
    private void onUserJoin(UserJoinEvent event) {
        User user = event.getUser();
        UserEconomy userEconomy = userEconomies.save(new UserEconomy(user));
        for (int i = 0; i < 5; i++) {
            userEconomies.save(userEconomy);
        }
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
                Bukkit.getPluginManager().callEvent(new UserReceiveCoinsEvent(summaryCoins, player));
            }
        }
    }

    private void startCacheCleaner() {
        final long PERIOD = 20 * 60 * 30;
        SCHEDULER.scheduleSyncRepeatingTask(module, this::clearCache, 0, 20 * 60 * 30);
        log("Cache cleaner started. Clean cache every " + PERIOD + " ticks");
    }

    private void clearCache() {
        int amount = userEconomies.getObjectCache().size(UserEconomy.class);
        userEconomies.clearObjectCache();
        log("Removed " + amount + " objects from cache");
    }

    private void log(String msg) {
        logger.info("[" + getClass().getSimpleName() + "] --- " + msg);
    }

}
