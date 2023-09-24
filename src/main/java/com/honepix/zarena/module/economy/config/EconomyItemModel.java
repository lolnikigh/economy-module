package com.honepix.zarena.module.economy.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
@AllArgsConstructor
public class EconomyItemModel {

    private Material material;
    private Component name;
    private List<Component> lore;
    private int customModelData;

    public boolean isCoinItem(ItemStack item) {
        if (item.getType() != material) return false;
        ItemMeta meta = item.getItemMeta();
        int cmd = meta.getCustomModelData();
        return cmd == customModelData;
    }

    public ItemStack createItem(int amount) {
        ItemStack coinItem = new ItemStack(material, amount);
        ItemMeta coinMeta = coinItem.getItemMeta();
        coinMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        coinMeta.displayName(name);
        coinMeta.lore(lore);
        coinMeta.setCustomModelData(customModelData);
        coinItem.setItemMeta(coinMeta);
        return coinItem;
    }

    @Override
    public String toString() {
        return "EconomyItemModel{" +
                "material=" + material +
                ", customModelData=" + customModelData +
                '}';
    }
}
