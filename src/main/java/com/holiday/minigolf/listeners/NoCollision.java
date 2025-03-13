package com.holiday.minigolf.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoCollision implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityDamageEvent event) {
        System.out.println("Evitando");
        Entity entity = event.getEntity();
        if (entity instanceof Snowball) {
            // Cancel the block change event to prevent the snowball from destroying blocks
            event.setCancelled(true);

            // Teleport the snowball to prevent it from falling
            Snowball snowball = (Snowball) entity;
            Location newLocation = snowball.getLocation().subtract(0, 1, 0);
            snowball.teleport(newLocation);
        }
    }
}
