package com.holiday.minigolf.listeners;

import com.holiday.minigolf.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class UnloadListener implements Listener {

    private final Main plugin = JavaPlugin.getPlugin(Main.class);

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event)
    {
        Entity[] ents = event.getChunk().getEntities();

        for (Entity ent : ents)
        {
            if (ent instanceof Snowball)
            {
                PersistentDataContainer c = ent.getPersistentDataContainer();
                if (!c.has(plugin.parKey, PersistentDataType.INTEGER))
                {
                    return;
                }
                ent.getWorld().dropItem(ent.getLocation(), plugin.golfBall);
                ent.remove();
            }
        }
    }
}
