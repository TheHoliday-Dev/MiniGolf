package com.holiday.minigolf.commands;

import com.holiday.minigolf.Main;
import com.holiday.minigolf.task.GolfBallRunnable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ReloadCommand implements CommandExecutor {

    private final Main plugin = JavaPlugin.getPlugin(Main.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        } else if (args[0].equalsIgnoreCase("spawn")) {

            Player player = (Player) sender;
            World world = player.getWorld();

            Snowball golfBall = (Snowball) world.spawnEntity(player.getLocation().add(0, 0.35, 0), EntityType.SNOWBALL);
            golfBall.getPersistentDataContainer().set(Main.getInstance().parKey, PersistentDataType.INTEGER, 0);

            ItemMeta meta = golfBall.getItem().getItemMeta();
            meta.getPersistentDataContainer().set(Main.getInstance().parKey, PersistentDataType.INTEGER, 1);
            golfBall.getItem().setItemMeta(meta);
            golfBall.setTicksLived(1);

            System.out.println(meta);

            golfBall.setCustomName("Par 0");
            golfBall.setCustomNameVisible(true);
            golfBall.setGravity(false);

            BukkitRunnable runnable = new GolfBallRunnable(golfBall);
            Main.getInstance().ballRunnables.put(golfBall, runnable);
            runnable.runTaskTimer(plugin, 0L, 2L);

            return true;

        } else if (args[0].equals("info")) {
            // Send message
            sender.sendMessage(ChatColor.WHITE + "[MiniGolf]" + ChatColor.GRAY + " Version " + plugin.getDescription().getVersion());
            return true;
        } else if (args[0].equals("reload")) {
            if (!sender.hasPermission("minigolf.reload")) {
                // Send message
                sender.sendMessage(ChatColor.WHITE + "[MiniGolf]" + ChatColor.RED + " You do not have permission to run this command.");
                return true;
            }
            // Reload config
            plugin.reloadConfig();

            // Send message
            sender.sendMessage(ChatColor.WHITE + "[MiniGolf]" + ChatColor.GRAY + " Config reloaded!");

            return true;
        } else if (args[0].equalsIgnoreCase("giveItem")) {
            if (!sender.hasPermission("op")) {
                sender.sendMessage(ChatColor.RED + "You must be an operator to use this command!");
                return false;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " giveItem <iron/driver/wedge/whistle/putter/ball>");
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
                return false;
            }
            Player p = (Player) sender;
            String item = args[1];
            if (item.equalsIgnoreCase("iron")) {
                p.getInventory().addItem(plugin.iron.clone());
            } else if (item.equalsIgnoreCase("driver")) {
                p.getInventory().addItem(plugin.driver.clone());
            } else if (item.equalsIgnoreCase("wedge")) {
                p.getInventory().addItem(plugin.wedge.clone());
            } else if (item.equalsIgnoreCase("whistle")) {
                p.getInventory().addItem(plugin.whistle.clone());
            } else if (item.equalsIgnoreCase("putter")) {
                p.getInventory().addItem(plugin.putter.clone());
            } else if (item.equalsIgnoreCase("ball")) {
                p.getInventory().addItem(plugin.golfBall.clone());
            } else {
                p.sendMessage(ChatColor.RED + "This item doesn't exist!");
                return false;
            }
            p.sendMessage(ChatColor.GREEN + "You were given the item!");
            return false;
        } else {
            return false;
        }
    }

    private Vector calculateVelocity(Vector direction, double multiplier) {
        direction.setY(0).normalize();
        return direction.multiply(multiplier);
    }
}
