package com.holiday.minigolf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReloadCommandCompleter implements TabCompleter {

    private static final List<String> COMMANDS = Arrays.asList("spawn", "info", "reload", "giveItem");
    private static final List<String> ITEMS = Arrays.asList("iron", "driver", "wedge", "whistle", "putter", "ball");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            for (String cmd : COMMANDS) {
                if (cmd.toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(cmd);
                }
            }
            return suggestions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("giveItem")) {
            List<String> itemSuggestions = new ArrayList<>();
            for (String item : ITEMS) {
                if (item.toLowerCase().startsWith(args[1].toLowerCase())) {
                    itemSuggestions.add(item);
                }
            }
            return itemSuggestions;
        }
        return Collections.emptyList();
    }
}
