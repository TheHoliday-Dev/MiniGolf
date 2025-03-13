package com.holiday.minigolf.listeners;

import com.holiday.minigolf.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PowerMeter implements Listener {

    private Main plugin;

    public PowerMeter(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void xpEvent(PlayerExpChangeEvent e) {
        e.setAmount(0);
    }

    @EventHandler
    public void playerSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            e.getPlayer().setLevel(0);
            e.getPlayer().setExp(0);
            new BukkitRunnable() {
                final Player p = e.getPlayer();
                boolean flipped = false;

                @Override
                public void run() {
                    if (!p.isSneaking()) {
                        p.setExp(0);
                        cancel();
                        return;
                    }
                    float currentXp = p.getExp();
                    if (!flipped) {
                        if (currentXp > 0.99F) {
                            flipped = true;
                            return;
                        }
                        p.setExp(currentXp + 0.01F);
                    } else {
                        if (currentXp < 0.01F) {
                            cancel();
                            return;
                        }
                        p.setExp(currentXp - 0.01F);
                    }
                }
            }.runTaskTimer(plugin, 1, 1);
        } else {
            e.getPlayer().setExp(0);
        }
    }
}
