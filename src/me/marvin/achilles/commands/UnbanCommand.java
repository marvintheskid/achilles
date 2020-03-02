package me.marvin.achilles.commands;

import com.google.gson.JsonObject;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.profile.impl.SimpleProfile;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandler;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.utils.Pair;
import me.marvin.achilles.utils.TimeFormatter;
import me.marvin.achilles.utils.sql.BatchContainer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static me.marvin.achilles.Language.Unban.*;
import static me.marvin.achilles.utils.etc.PlayerUtils.getPlayerName;
import static me.marvin.achilles.utils.etc.StringUtils.colorize;
import static me.marvin.achilles.utils.etc.StringUtils.formatFully;

public class UnbanCommand extends WrappedCommand {
    public UnbanCommand() {
        super("unban");
        setDescription("Bans people.");
        setPermission("achilles.ban.lift");
        setPermissionMessage(colorize(Language.Other.NO_PERMISSION));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(colorize(USAGE));
            return false;
        }

        UUID issuer;
        String issuerName;

        if (sender instanceof Player) {
            issuer = ((Player) sender).getUniqueId();
            issuerName = sender.getName();
        } else {
            issuer = Punishment.CONSOLE_UUID;
            issuerName = Language.Other.CONSOLE_NAME;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        String targetName = getPlayerName(args[0]);

        SimpleProfile profile = new SimpleProfile(target.getUniqueId());
        Optional<Ban> currentBan = profile.getActive(Ban.class);
        Pair<String, Boolean> formatted = formatFully(args, 1, DEFAULT_REASON);

        if (currentBan.isPresent()) {
            BatchContainer[] containers = new BatchContainer[profile.getCache().get(Ban.class).size()];
            int index = 0;
            for (Ban ban : profile.getPunishments(Ban.class)) {
                containers[index++] = ban.createLiftBatch();
            }

            Achilles.getConnection().batchUpdate(PunishmentHandler.BAN_HANDLER.getLiftQuery(), (result) -> {
                String localMsg = MESSAGE
                    .replace("{issuer}", issuerName)
                    .replace("{target}", targetName)
                    .replace("{reason}", formatted.getKey())
                    .replace("{silent}", formatted.getValue() ? SILENT : "")
                    .replace("{server}", Variables.Database.SERVER_NAME);

                Bukkit.broadcast(colorize(localMsg), "achilles.alerts");

                JsonObject alertData = new JsonObject();
                alertData.addProperty("message", ALERT_MESSAGE
                    .replace("{issuer}", issuerName)
                    .replace("{target}", targetName)
                    .replace("{reason}", formatted.getKey())
                    .replace("{silent}", formatted.getValue() ? SILENT : "")
                    .replace("{server}", Variables.Database.SERVER_NAME));

                Message message = new Message(MessageType.MESSAGE, alertData);

                Achilles.getMessenger().sendMessage(message);
            }, containers);
            return true;
        }

        sender.sendMessage(colorize(Language.Other.NOT_PUNISHED));
        return false;
    }
}
