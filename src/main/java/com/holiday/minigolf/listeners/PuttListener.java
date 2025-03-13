package com.holiday.minigolf.listeners;

import com.holiday.minigolf.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PuttListener implements Listener {

    private final Main plugin = JavaPlugin.getPlugin(Main.class);

    @EventHandler
    public void onPutt(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !isGolfClub(item)) {
            return;
        }

        event.setCancelled(true);

        List<Entity> nearbyEntities = player.getNearbyEntities(5.5, 5.5, 5.5);

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        Vector eyeVector = eyeLocation.toVector();

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Snowball) {

                try {

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                Snowball ball = (Snowball) entity;

                PersistentDataContainer container = ball.getPersistentDataContainer();
                System.out.println(ball.getItem().getItemMeta());
                System.out.println(ball.getItem().getItemMeta().getPersistentDataContainer().has(Main.getInstance().parKey, PersistentDataType.INTEGER));
                System.out.println(container.has(Main.getInstance().parKey, PersistentDataType.INTEGER));

                if (!container.has(plugin.parKey, PersistentDataType.INTEGER)) {
                    continue;
                }

                Vector ballVector = ball.getLocation().toVector().subtract(eyeVector);

                if (direction.angle(ballVector) < 0.15f) {
                    if (!canHitBall(ball, player.getUniqueId())) {
                        continue;
                    }

                    if (Main.getInstance().ballRunnables.containsKey(ball)) {
                        BukkitRunnable runnable = Main.getInstance().ballRunnables.get(ball);
                        runnable.cancel();
                        Main.getInstance().ballRunnables.remove(ball);
                    }

                    double velocityMultiplier = getVelocityMultiplier(item, player);

                    Vector velocity = calculateVelocity(direction, velocityMultiplier);

                    ball.setVelocity(velocity);

                    int par = container.get(plugin.parKey, PersistentDataType.INTEGER) + 1;
                    container.set(plugin.parKey, PersistentDataType.INTEGER, par);
                    ball.setCustomName("Par " + par);

                    Location lastPosition = ball.getLocation();
                    container.set(plugin.xKey, PersistentDataType.DOUBLE, lastPosition.getX());
                    container.set(plugin.yKey, PersistentDataType.DOUBLE, lastPosition.getY());
                    container.set(plugin.zKey, PersistentDataType.DOUBLE, lastPosition.getZ());

                    plugin.golfBalls.add(ball);
                    ball.setTicksLived(1);

                    playHitEffects(player, velocity);
                }
            }
        }
    }

    private boolean isGolfClub(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta != null && (meta.hasCustomModelData() || meta.getDisplayName().contains("Golf Ball"));
    }

    private boolean canHitBall(Snowball ball, UUID playerId) {
        for (Map.Entry<UUID, Snowball> entry : plugin.lastPlayerBall.entrySet()) {
            if (entry.getValue().equals(ball) && !entry.getKey().equals(playerId)) {
                return false;
            }
        }
        return true;
    }

    private double getVelocityMultiplier(ItemStack item, Player player) {
        boolean isPutter = item.getType() == Material.WOODEN_SHOVEL;
        boolean isIron = item.getType() == Material.IRON_SWORD;
        boolean isWedge = item.getType() == Material.GOLDEN_SHOVEL;
        boolean isDriver = item.getType() == Material.DIAMOND_AXE;

        boolean isSneaking = player.isSneaking();
        boolean isCrit = player.getVelocity().getY() < -0.08;

        double velocityMultiplier;
        if (isIron) {
            velocityMultiplier = (isCrit ? 1 : isSneaking ? 0.6666 : 0.8333) * (isSneaking ? plugin.getConfig().getInt("powerMeterMultiplier") - 1 + player.getExp() : 1);
        } else if (isDriver) {
            velocityMultiplier = (isCrit ? 1 : isSneaking ? 0.6666 : 0.8333) * 1.4 * (isSneaking ? plugin.getConfig().getInt("powerMeterMultiplier") - 1 + player.getExp() : 1);
        } else if (isPutter) {
            velocityMultiplier = (isCrit ? 0.5 : isSneaking ? 0.1666 : 0.3333) * (isSneaking ? plugin.getConfig().getInt("powerMeterMultiplier") - 1 + player.getExp() : 1);
        } else if (isWedge) {
            velocityMultiplier = (isCrit ? 0 : isSneaking ? 0.125 : 0.25) * (isSneaking ? plugin.getConfig().getInt("powerMeterMultiplier") - 1 + player.getExp() : 1);
        } else {
            velocityMultiplier = 1.0;
        }
        return velocityMultiplier;
    }

    private Vector calculateVelocity(Vector direction, double multiplier) {
        direction.setY(0).normalize();
        return direction.multiply(multiplier);
    }

    private void playHitEffects(Player player, Vector velocity) {
        World world = player.getWorld();
        boolean isCrit = velocity.getY() < -0.08;
        boolean isSneaking = player.isSneaking();
        float volume = isCrit ? 1f : isSneaking ? 0.5f : 0.75f;
        float pitch = isCrit ? 1.25f : isSneaking ? 0.5f : 0.75f;

        world.spawnParticle(Particle.CRIT, player.getLocation(), 15, 0, 0, 0, 0.25);
        world.playSound(player.getLocation(), Sound.BLOCK_METAL_HIT, volume, pitch);
    }
}
