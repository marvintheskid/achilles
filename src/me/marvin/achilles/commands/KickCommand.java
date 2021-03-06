package me.marvin.achilles.commands;

import com.google.gson.JsonObject;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Kick;
import me.marvin.achilles.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.marvin.achilles.Language.Kick.*;
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

public class KickCommand extends WrappedCommand {
    public KickCommand() {
        super("kick");
        setDescription("Kicks people.");
        setPermission("achilles.kick.issue");
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
        Pair<String, Boolean> formatted = formatFully(args, 1, DEFAULT_REASON);
        Kick kick = new Kick(issuer, target.getUniqueId(), formatted.getKey());
        kick.issue();

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

        //TODO: implement cross-server kick better

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
