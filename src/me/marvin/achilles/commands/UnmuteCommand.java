package me.marvin.achilles.commands;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.profile.impl.FullProfile;
import me.marvin.achilles.profile.impl.SimpleProfile;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandler;
import me.marvin.achilles.punishment.impl.Mute;
import me.marvin.achilles.utils.Pair;
import me.marvin.achilles.utils.sql.BatchContainer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.marvin.achilles.Language.Unmute.*;
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

//FIXME
public class UnmuteCommand extends WrappedCommand {
    public UnmuteCommand() {
        super("unmute");
        setDescription("Unmutes people.");
        setPermission("achilles.mute.lift");
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

        Profile profile = target.isOnline() ?
            Preconditions.checkNotNull(Achilles.getProfileHandler().getProfiles().get(target.getUniqueId())) : new SimpleProfile(target.getUniqueId());

        Optional<Mute> currentBan = profile.getActive(Mute.class);
        Pair<String, Boolean> formatted = formatFully(args, 1, DEFAULT_REASON);

        if (currentBan.isPresent()) {
            List<Mute> punishments = profile.getPunishments(Mute.class)
                .stream()
                .filter(LiftablePunishment::isActive)
                .collect(Collectors.toList());
            BatchContainer[] containers = new BatchContainer[punishments.size()];
            for (int i = 0; i < punishments.size(); i++) {
                Mute mute = punishments.get(i);
                mute.setLiftedBy(issuer);
                mute.setLiftReason(formatted.getKey());
                containers[i] = mute.createLiftBatch();
            }

            Achilles.getConnection().batchUpdate(PunishmentHandler.BAN_HANDLER.getLiftQuery(), (result) -> {}, containers);

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

            if (!target.isOnline()) {
                JsonObject updateData = new JsonObject();
                updateData.addProperty("uuid", target.getUniqueId().toString());

                Message updateReq = new Message(MessageType.DATA_UPDATE, updateData);
                Achilles.getMessenger().sendMessage(updateReq);
            }

            return true;
        }

        sender.sendMessage(colorize(Language.Other.NOT_PUNISHED));
        return false;
    }
}
