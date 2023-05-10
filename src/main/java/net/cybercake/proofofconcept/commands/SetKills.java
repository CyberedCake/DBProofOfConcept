package net.cybercake.proofofconcept.commands;

import net.cybercake.cyberapi.common.basic.NumberUtils;
import net.cybercake.cyberapi.spigot.chat.TabCompleteType;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import net.cybercake.proofofconcept.database.Database;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetKills extends SpigotCommand {

    public SetKills() {
        super(
                newCommand("setkills")
                        .setTabCompleteType(TabCompleteType.SEARCH)
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(UChat.component("&cCan only run this command as a " + Player.class)); return true;
        }

        if(args.length < 1) { // increment player kills by one if no argument is given
            Database.execute(player.getUniqueId(), (user) -> { user.kills += 1; return user; });
            sender.sendMessage(UChat.component("&aAdded &e1 &ato your kill count!"));
            return true;
        }
        if(args[0].equalsIgnoreCase("--delete")) { // deletes self from database
            Database.delete(player.getUniqueId());
            sender.sendMessage(UChat.component("&aYou &cremoved &ayourself from the database!")); return true;
        }
        if(!NumberUtils.isInteger(args[0])) { // ensure args[0] is an integer (going to be used later)
            sender.sendMessage(UChat.component("&cRequire an integer at the first argument!")); return true;
        }
        int argument = Integer.parseInt(args[0]);
        if(!NumberUtils.isBetweenEquals(argument, 0, 9999)) { // don't want to allow a really high number (not a database limitation -- just cuz)
            sender.sendMessage(UChat.component("&cRequire the integer to be between &b0 - 9999&c!"));
        }
        Database.execute(player.getUniqueId(), (user) -> {
            user.kills = argument;
            UChat.broadcast(user.playtime + "");
            return user;
        });

        sender.sendMessage(UChat.component("&aSet your kill count to &e" + argument));
        return true;
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if(args.length == 1) {
            List<String> potentialCompletions = UTabComp.getIntegers(args[0], 0, 9999);
            if(args[0].startsWith("--")) potentialCompletions.add("--delete");
            return potentialCompletions;
        };
        return null;
    }
}
