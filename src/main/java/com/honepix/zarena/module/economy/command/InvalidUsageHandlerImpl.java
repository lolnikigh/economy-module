package com.honepix.zarena.module.economy.command;

import com.honepix.zarena.module.economy.config.EconomyMessages;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.List;

import static com.honepix.lib.util.ComponentUtils.fromLegacy;

@AllArgsConstructor
public class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSender> {

    private EconomyMessages economyMessages;

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        List<String> schematics = schematic.getSchematics();

        if (schematics.size() == 1) {
            sender.sendMessage(fromLegacy("&cНеверное использование команды &8>> &7" + schematics.get(0)));
            return;
        }

        sender.sendMessage(fromLegacy("&cНеверное использование команды"));
        for (String sch : schematics) {
            sender.sendMessage(fromLegacy("&8 >> &7" + sch));
        }
    }
}
