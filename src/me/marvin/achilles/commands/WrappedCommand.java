package me.marvin.achilles.commands;

import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_8_R3.command.CraftConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


/*
 * Copyright (c) 2019-Present marvintheskid (Kovács Márton)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

@Data
public abstract class WrappedCommand implements CommandExecutor {
    private List<String> aliases;
    private String command;
    private String description;
    private String usage;
    private String permission;
    private String permissionMessage;

    public WrappedCommand(String command) {
        this.command = command;
    }

    public void setExecutor(JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand(this.command);
        if (aliases != null) command.setAliases(aliases);
        if (description != null) command.setDescription(description);
        if (usage != null) command.setUsage(usage);
        if (permission != null) command.setPermission(permission);
        if (permissionMessage != null) command.setPermissionMessage(permissionMessage);
        command.setExecutor(this);
    }

    public abstract boolean execute(CommandSender sender, String label, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, label, args);
    }
}
