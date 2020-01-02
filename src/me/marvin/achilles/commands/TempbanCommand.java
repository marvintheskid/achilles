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
import me.marvin.achilles.utils.PeriodMatcher;
import me.marvin.achilles.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static me.marvin.achilles.Language.Tempban.*;
import static me.marvin.achilles.utils.etc.PlayerUtils.canIssue;
import static me.marvin.achilles.utils.etc.PlayerUtils.getPlayerName;
import static me.marvin.achilles.utils.etc.StringUtils.colorize;
import static me.marvin.achilles.utils.etc.StringUtils.formatFully;

public class TempbanCommand extends WrappedCommand {
    public TempbanCommand() {
        super("tempban");
        setDescription("Bans people.");
        setPermission("achilles.tempban.issue");
        setPermissionMessage(colorize(Language.Other.NO_PERMISSION));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
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

        long until = PeriodMatcher.parsePeriod(args[1]);

        if (until == -1) {
            sender.sendMessage(colorize(Language.Other.WRONG_DURATION_FORMAT));
            return false;
        }

        if (sender instanceof Player) {
            if (!sender.hasPermission("achilles.ban.issue")) {
                Pair<Boolean, Long> canIssue = canIssue((Player) sender, "ban", until);
                if (!canIssue.getKey()) {
                    sender.sendMessage(colorize(MAX_LENGTH.replace("{highest}", PeriodMatcher.parsePeriod(canIssue.getValue()))));
                    return false;
                }
            }
        }

        if (currentBan.isPresent()) {
            if (!sender.hasPermission("achilles.tempban.override") && !currentBan.get().isPermanent() && currentBan.get().getUntil() > System.currentTimeMillis() + until) {
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

        Pair<String, Boolean> formatted = formatFully(args, 2, DEFAULT_REASON);
        Ban ban = new Ban(issuer, target.getUniqueId(), System.currentTimeMillis() + until, formatted.getKey());
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
        Message message = new Message(MessageType.MESSAGE, alertData);

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getUniqueId()).kickPlayer(colorize(punishmentMsg));

            Achilles.getMessenger().sendMessage(message);
        } else {
            JsonObject kickData = new JsonObject();
            kickData.addProperty("uuid", target.getUniqueId().toString());
            kickData.addProperty("message", punishmentMsg);

            Message kickReq = new Message(MessageType.KICK_REQUEST, kickData);
            Achilles.getMessenger().sendMessage(message);
            Achilles.getMessenger().sendMessage(kickReq);
        }

        return true;
    }
}
