package me.marvin.achilles.commands;

import com.google.gson.JsonObject;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.profile.impl.SimpleProfile;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Mute;
import me.marvin.achilles.utils.Pair;
import me.marvin.achilles.utils.PeriodMatcher;
import me.marvin.achilles.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static me.marvin.achilles.Language.Tempmute.*;
import static me.marvin.achilles.utils.etc.PlayerUtils.canIssue;
import static me.marvin.achilles.utils.etc.PlayerUtils.getPlayerName;
import static me.marvin.achilles.utils.etc.StringUtils.colorize;
import static me.marvin.achilles.utils.etc.StringUtils.formatFully;

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

public class TempmuteCommand extends WrappedCommand {
    public TempmuteCommand() {
        super("tempmute");
        setDescription("Mutes people.");
        setPermission("achilles.tempmute.issue");
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
        Optional<Mute> currentMute = profile.getActive(Mute.class);

        long until = PeriodMatcher.parsePeriod(args[1]);

        if (until == -1) {
            sender.sendMessage(colorize(Language.Other.WRONG_DURATION_FORMAT));
            return false;
        }

        if (sender instanceof Player) {
            if (!sender.hasPermission("achilles.tempmute.issue")) {
                Pair<Boolean, Long> canIssue = canIssue((Player) sender, "mute", until);
                if (!canIssue.getKey()) {
                    sender.sendMessage(colorize(MAX_LENGTH.replace("{highest}", PeriodMatcher.parsePeriod(canIssue.getValue()))));
                    return false;
                }
            }
        }

        if (currentMute.isPresent()) {
            if (!sender.hasPermission("achilles.tempmute.override") && !currentMute.get().isPermanent() && currentMute.get().getUntil() > System.currentTimeMillis() + until) {
                sender.sendMessage(colorize(Language.Other.NO_PERMISSION_TO_OVERRIDE));
                return false;
            } else {
                profile.getCache().get(Mute.class).forEach(punishment -> {
                    Mute active = (Mute) punishment;
                    active.setLiftedBy(issuer);
                    active.setLiftReason(Language.Other.OVERRIDE_REASON);
                    active.lift();
                });
            }
        }

        Pair<String, Boolean> formatted = formatFully(args, 2, DEFAULT_REASON);
        Mute mute = new Mute(issuer, target.getUniqueId(), System.currentTimeMillis() + until, formatted.getKey());
        mute.issue();

        String localMsg = MESSAGE
            .replace("{issuer}", issuerName)
            .replace("{target}", targetName)
            .replace("{reason}", formatted.getKey())
            .replace("{silent}", formatted.getValue() ? SILENT : "")
            .replace("{server}", Variables.Database.SERVER_NAME)
            .replace("{remaining}", TimeFormatter.formatTime(mute.getRemaining()))
            .replace("{until}", Variables.Date.DATE_FORMAT.format(new Date(mute.getUntil())));
        String punishmentMsg = PUNISHMENT_MESSAGE
            .replace("{issuer}", issuerName)
            .replace("{target}", targetName)
            .replace("{reason}", formatted.getKey())
            .replace("{silent}", formatted.getValue() ? SILENT : "")
            .replace("{server}", Variables.Database.SERVER_NAME)
            .replace("{remaining}", TimeFormatter.formatTime(mute.getRemaining()))
            .replace("{until}", Variables.Date.DATE_FORMAT.format(new Date(mute.getUntil())));

        JsonObject alertData = new JsonObject();
        alertData.addProperty("message", ALERT_MESSAGE
            .replace("{issuer}", issuerName)
            .replace("{target}", targetName)
            .replace("{reason}", formatted.getKey())
            .replace("{silent}", formatted.getValue() ? SILENT : "")
            .replace("{server}", Variables.Database.SERVER_NAME)
            .replace("{remaining}", TimeFormatter.formatTime(mute.getRemaining()))
            .replace("{until}", Variables.Date.DATE_FORMAT.format(new Date(mute.getUntil()))));

        Bukkit.broadcast(colorize(localMsg), "achilles.alerts");
        Message message = new Message(MessageType.MESSAGE, alertData);

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getUniqueId()).sendMessage(colorize(punishmentMsg));
            if (Achilles.getProfileHandler().getProfiles().containsKey(target.getUniqueId())) {
                Achilles.getProfileHandler().getProfiles().get(target.getUniqueId()).getPunishments().add(mute);
            }
            Achilles.getMessenger().sendMessage(message);
        } else {
            JsonObject updateData = new JsonObject();
            updateData.addProperty("uuid", target.getUniqueId().toString());

            Message updateReq = new Message(MessageType.DATA_UPDATE, updateData);
            Achilles.getMessenger().sendMessage(message);
            Achilles.getMessenger().sendMessage(updateReq);
        }

        return true;
    }
}
