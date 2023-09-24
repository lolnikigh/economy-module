package com.honepix.zarena.module.economy.command;

import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class PermissionHandlerImpl implements dev.rollczi.litecommands.handle.PermissionHandler<CommandSender> {

    @Override
    public void handle(CommandSender commandSender, LiteInvocation liteInvocation, RequiredPermissions requiredPermissions) {
        commandSender.sendMessage(Component.text("Недостаточно прав").color(NamedTextColor.RED));
    }
}
