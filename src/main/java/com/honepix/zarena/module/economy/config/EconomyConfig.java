package com.honepix.zarena.module.economy.config;

import com.honepix.lib.util.ComponentUtils;
import com.honepix.zarena.module.economy.EconomyModule;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@Getter
public class EconomyConfig {

    private final EconomyModule module;
    private final Logger logger;

    private final Map<String, Double> COIN_MODIFIER_MAP = new HashMap<>();
    private final String PERMISSION_PREFIX = "zarena.economy.coin-modifier.";

    private Component formatMessage;
    private int exchangeRate;
    private final EconomyItemModel itemModel;

    public EconomyConfig(EconomyModule module) {
        this.module = module;
        this.logger = module.getLogger();
        Configuration configuration = module.getConfig();
        itemModel = getEconomyItemModel(configuration);
        log("Loaded economy item model " + itemModel);
        loadGroupCoinModifier(configuration);
        exchangeRate = configuration.getInt("exchange-rate");
        log("Exchange rate = " + exchangeRate);
        formatMessage = ComponentUtils.fromLegacy(configuration.getString("message.received-coins"));
    }

    public ItemStack createCoinItem(int amount) {
        return itemModel.createItem(amount);
    }

    public double getCoinModifier(Player player) {
        String permission = COIN_MODIFIER_MAP.keySet().stream()
                .filter(player::hasPermission)
                .findFirst().orElse(PERMISSION_PREFIX + "default");
        return COIN_MODIFIER_MAP.get(permission);
    }

    private void loadGroupCoinModifier(Configuration configuration) {
        ConfigurationSection section = configuration.getConfigurationSection("group.coin-modifier");
        Validate.notNull(section);
        Set<String> groups = section.getKeys(false);
        groups.forEach(groupName -> {
            double modifier = section.getDouble(groupName, 1);
            String permission = PERMISSION_PREFIX + groupName;
            COIN_MODIFIER_MAP.put(permission, modifier);
        });
        log("Loaded " + COIN_MODIFIER_MAP.size() + " coin-modifier permissions " + COIN_MODIFIER_MAP);
    }

    private EconomyItemModel getEconomyItemModel(Configuration configuration) {
        ConfigurationSection coinItemSection = configuration.getConfigurationSection("coin-item");
        Validate.notNull(coinItemSection);
        String materialName = coinItemSection.getString("material");
        Material material = Material.valueOf(materialName);
        Validate.isTrue(!material.isAir(), "material cannot be air");
        int customModelData = coinItemSection.getInt("custom-model-data");
        String name = coinItemSection.getString("name");
        Component nameComponent = toComponent(name);
        List<String> lore = coinItemSection.getStringList("lore");
        List<Component> loreComponent = lore.stream()
                .map(this::toComponent)
                .toList();
        return new EconomyItemModel(material, nameComponent, loreComponent, customModelData);
    }

    private Component toComponent(String text) {
        return ComponentUtils.fromLegacy(text)
                .decoration(TextDecoration.ITALIC, false);
    }

    private void log(String msg) {
        logger.info("[" + getClass().getSimpleName() + "] --- " + msg);
    }

}
