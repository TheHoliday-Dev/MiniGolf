package com.holiday.minigolf.task;

import com.holiday.minigolf.Main;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class GolfBallTask extends BukkitRunnable {

    private final Main plugin;
    private final FireworkEffect fx;
    private final Map<Snowball, Location> lastValidLocations;

    public GolfBallTask() {
        this.plugin = Main.getInstance();
        this.fx = FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BALL).build();
        this.lastValidLocations = new HashMap<>();
    }

    @Override
    public void run() {
        Iterator<Snowball> iterator = plugin.golfBalls.iterator();
        while (iterator.hasNext()) {
            Snowball ball = iterator.next();
            if (!ball.isValid()) {
                iterator.remove();
                continue;
            }

            if (ball.getTicksLived() > 1200) {
                ball.getWorld().dropItem(ball.getLocation(), plugin.golfBall);
                ball.remove();
                iterator.remove();
                return;
            }

            Location loc = ball.getLocation();
            Material blockType = loc.subtract(0, 0.1, 0).getBlock().getType();
            Vector velocity = ball.getVelocity();

            switch (blockType) {
                case CAULDRON:
                    handleCauldronCollision(ball, iterator);
                    break;
                case SOUL_SAND:
                    ball.setVelocity(new Vector(0, ball.getVelocity().getY(), 0));
                case AIR:
                    ball.setGravity(true);
                    break;
                case SLIME_BLOCK:
                    ball.setVelocity(velocity.setY(0.25));
                    break;
                case WATER:
                    ball.setVelocity(new Vector(0, 0, 0));
                    ball.teleport(lastValidLocations.get(ball));
                    break;
                case ICE:
                case PACKED_ICE:
                case BLUE_ICE:
                    break;
                case SAND:
                case RED_SAND:
                    ball.setVelocity(velocity.multiply(0.9));
                    break;
                default:
                    ball.setVelocity(velocity.multiply(0.975));
                    break;
            }
            handleSurroundingBlocks(ball);

            if (!blockType.equals(Material.WATER)) {
                lastValidLocations.put(ball, ball.getLocation());
            }
        }
    }

    private Location findSafeLocation(Snowball ball) {
        Location loc = ball.getLocation();
        World world = loc.getWorld();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location checkLoc = loc.clone().add(x, 0, z);
                Material blockType = checkLoc.getBlock().getType();
                if (blockType != Material.WATER) {

                    return checkLoc;
                }
            }
        }
        return new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ());
    }

    private void handleSurroundingBlocks(Snowball ball) {
        Location loc = ball.getLocation();
        Vector velocity = ball.getVelocity();

        Material blockTypeAbove = loc.clone().add(0, 1.1, 0).getBlock().getType();
        Material blockTypeFront = loc.clone().add(velocity.clone().normalize()).getBlock().getType();
        Material blockTypeBack = loc.clone().subtract(velocity.clone().normalize()).getBlock().getType();

        double yImpulse = 0.0;

        if (plugin.isSlab(blockTypeAbove)) {
            yImpulse += getConfiguredImpulse(blockTypeAbove);
        }

        if (plugin.isSlab(blockTypeFront)) {
            yImpulse += getConfiguredImpulse(blockTypeFront);
        }

        if (plugin.isSlab(blockTypeBack)) {
            yImpulse += getConfiguredImpulse(blockTypeBack);
        }

        Vector newVelocity = ball.getVelocity().clone().add(new Vector(0, yImpulse, 0));
        ball.setVelocity(newVelocity);

        if (yImpulse > 0.0) {
            handleSlabCollision(ball);
        }
    }


    private double getConfiguredImpulse(Material material) {
        double defaultImpulse = 0.5;

        if (plugin.getConfig().contains("blocks-slab." + material.name())) {
            return plugin.getConfig().getDouble("blocks-slab." + material.name(), defaultImpulse);
        } else {
            getLogger().warning("Impulse for " + material.name() + " slab is not configured in the plugin.");
            return defaultImpulse;
        }
    }

    private void handleSlabCollision(Snowball ball) {
        Location loc = ball.getLocation();
        Vector velocity = ball.getVelocity();

        // Slightly lift the ball to simulate passing over the slab
        if (velocity.getY() <= 0) {
            ball.setVelocity(new Vector(velocity.getX(), 0.5, velocity.getZ()));
        }
    }

    private void handleCauldronCollision(Snowball ball, Iterator<Snowball> iterator) {
        Firework firework = (Firework) ball.getWorld().spawnEntity(ball.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(1);
        meta.addEffect(fx);
        firework.setFireworkMeta(meta);

        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(plugin, 20L);

        ball.getWorld().dropItem(ball.getLocation(), plugin.golfBall);
        sendScoreMessage(ball);
        ball.remove();
        iterator.remove();
    }

    private void sendScoreMessage(Snowball ball) {
        for (Map.Entry<UUID, Snowball> entry : plugin.lastPlayerBall.entrySet()) {
            if (entry.getValue().equals(ball)) {
                int par = ball.getPersistentDataContainer().get(plugin.parKey, PersistentDataType.INTEGER);
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    String msg = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("scoreMsg"))
                            .replace("&v1", player.getName())
                            .replace("&v2", Integer.toString(par));
                    player.sendMessage(msg);
                }
                break;
            }
        }
    }
}
