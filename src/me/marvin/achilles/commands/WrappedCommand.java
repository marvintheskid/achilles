package me.marvin.achilles.commands;

import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


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
