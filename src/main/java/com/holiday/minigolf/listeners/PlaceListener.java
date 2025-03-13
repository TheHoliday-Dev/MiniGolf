package com.holiday.minigolf.listeners;

import com.holiday.minigolf.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlaceListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getItemInHand().getType() == Material.SNOWBALL) {
                if (isGolfItem(player.getItemInHand())) {
                    Snowball snowball = player.getWorld().spawn(event.getClickedBlock().getLocation().add(0.5, 1.29, 0.5), Snowball.class);
                    snowball.getPersistentDataContainer().set(Main.getInstance().parKey, PersistentDataType.INTEGER, 0);
                    snowball.setTicksLived(1);
                    snowball.setCustomName("Par 0");
                    snowball.setCustomNameVisible(true);
                    snowball.setGravity(false);
                }
            }
        }
    }

    public boolean isGolfItem(ItemStack item) {
        if (item.getItemMeta() != null) {
            return item.getItemMeta().getPersistentDataContainer().has(Main.getInstance().parKey, PersistentDataType.INTEGER);
        }
        return false;
    }
}
