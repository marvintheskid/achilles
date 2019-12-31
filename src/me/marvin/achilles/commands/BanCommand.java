package me.marvin.achilles.commands;

import com.google.gson.JsonObject;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.profile.impl.SimpleProfile;
import me.marvin.achilles.punishment.ExpirablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static me.marvin.achilles.Language.Ban.*;
import static me.marvin.achilles.utils.etc.StringUtils.*;
import static me.marvin.achilles.utils.etc.PlayerUtils.*;

public class BanCommand extends WrappedCommand {
    public BanCommand() {
        super("ban");
        setDescription("Bans people.");
        setPermission("achilles.ban.issue");
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

        if (currentBan.isPresent()) {
            if (!sender.hasPermission("achilles.ban.override") && currentBan.get().isPermanent()) {
                sender.sendMessage(colorize(Language.Other.NO_PERMISSION_TO_OVERRIDE));
                return false;
            } else {
                profile.getCache().get(Ban.class).forEach(punishment -> {
                    Ban active = (Ban) punishment;
                    active.setLiftedBy(issuer);
                    active.setLiftReason(Language.Other.OVERRIDE_REASON);
                    active.lift();
                });
            }
        }

        Pair<String, Boolean> formatted = formatFully(args, DEFAULT_REASON);
        Ban ban = new Ban(issuer, target.getUniqueId(), ExpirablePunishment.PERMANENT_PUNISHMENT, formatted.getKey());
        ban.issue();

        String localMsg = MESSAGE
            .replace("{issuer}", issuerName)
            .replace("{target}", targetName)
            .replace("{reason}", formatted.getKey())
            .replace("{silent}", formatted.getValue() ? SILENT : "")
            .replace("{server}", Variables.Database.SERVER_NAME);
        String punishmentMsg = PUNISHMENT_MESSAGE
            .replace("{issuer}", issuerName)
            .replace("{target}", targetName)
            .replace("{reason}", formatted.getKey())
            .replace("{silent}", formatted.getValue() ? SILENT : "")
            .replace("{server}", Variables.Database.SERVER_NAME);

        JsonObject alertData = new JsonObject();
        alertData.addProperty("message", ALERT_MESSAGE
            .replace("{issuer}", issuerName)
            .replace("{target}", targetName)
            .replace("{reason}", formatted.getKey())
            .replace("{silent}", formatted.getValue() ? SILENT : "")
            .replace("{server}", Variables.Database.SERVER_NAME));

        Bukkit.broadcast(colorize(localMsg), "achilles.alerts");

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getUniqueId()).kickPlayer(colorize(punishmentMsg));

            Message message = new Message(MessageType.MESSAGE, alertData);
            Achilles.getMessenger().sendMessage(message);
        } else {
            JsonObject kickData = new JsonObject();
            kickData.addProperty("uuid", target.getUniqueId().toString());
            kickData.addProperty("message", punishmentMsg);

            Message kickReq = new Message(MessageType.KICK_REQUEST, kickData);
            Message message = new Message(MessageType.MESSAGE, alertData);
            Achilles.getMessenger().sendMessage(message);
            Achilles.getMessenger().sendMessage(kickReq);
        }

        return true;
    }
}
