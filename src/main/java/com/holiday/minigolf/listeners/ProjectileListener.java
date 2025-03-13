package com.holiday.minigolf.listeners;

import com.holiday.minigolf.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class ProjectileListener implements Listener {

    private final Main plugin = JavaPlugin.getPlugin(Main.class);

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Snowball)) {
            return;
        }

        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(plugin.parKey, PersistentDataType.INTEGER)) {
            return;
        }

        World world = entity.getWorld();
        Location location = entity.getLocation();
        Vector velocity = entity.getVelocity();

        Snowball newBall = (Snowball) world.spawnEntity(location, EntityType.SNOWBALL);
        newBall.setGravity(entity.hasGravity());
        plugin.golfBalls.add(newBall);

        updateLastPlayerBall(entity, newBall);

        int par = container.get(plugin.parKey, PersistentDataType.INTEGER);
        PersistentDataContainer newBallContainer = newBall.getPersistentDataContainer();
        newBallContainer.set(plugin.parKey, PersistentDataType.INTEGER, par);
        newBall.setCustomName("Par " + par);
        newBall.setCustomNameVisible(true);

        double lastX = container.get(plugin.xKey, PersistentDataType.DOUBLE);
        double lastY = container.get(plugin.yKey, PersistentDataType.DOUBLE);
        double lastZ = container.get(plugin.zKey, PersistentDataType.DOUBLE);
        newBallContainer.set(plugin.xKey, PersistentDataType.DOUBLE, lastX);
        newBallContainer.set(plugin.yKey, PersistentDataType.DOUBLE, lastY);
        newBallContainer.set(plugin.zKey, PersistentDataType.DOUBLE, lastZ);

        if (event.getHitBlockFace() == null) {
            handleNonBlockHit(newBall, lastX, lastY, lastZ);
            return;
        }

        Material material = event.getHitBlock().getType();
        handleBlockHit(newBall, velocity, event.getHitBlockFace(), material);
    }

    private void updateLastPlayerBall(Entity oldBall, Snowball newBall) {
        for (Map.Entry<UUID, Snowball> entry : plugin.lastPlayerBall.entrySet()) {
            if (entry.getValue().equals(oldBall)) {
                entry.setValue(newBall);
                break;
            }
        }
    }

    private void handleNonBlockHit(Snowball ball, double lastX, double lastY, double lastZ) {
        ball.setVelocity(new Vector(0, 0, 0));
        ball.teleport(new Location(ball.getWorld(), lastX, lastY, lastZ));
        ball.setGravity(false);
    }

    private void handleBlockHit(Snowball ball, Vector velocity, BlockFace blockFace, Material material) {
        switch (blockFace) {
            case NORTH:
            case SOUTH:
                handleXHit(ball, velocity, material);
                break;
            case EAST:
            case WEST:
                handleZHit(ball, velocity, material);
                break;
            case UP:
            case DOWN:
                handleYHit(ball, velocity, material);
                break;
            default:
                break;
        }
    }

    private void handleXHit(Snowball ball, Vector velocity, Material material) {
        if (material == Material.HONEY_BLOCK) {
            velocity.setZ(0);
        } else if (material == Material.SLIME_BLOCK) {
            velocity.setZ(Math.copySign(0.25, -velocity.getZ()));
        } else {
            velocity.setZ(-velocity.getZ());
        }
        ball.setVelocity(velocity);
    }

    private void handleZHit(Snowball ball, Vector velocity, Material material) {
        if (material == Material.HONEY_BLOCK) {
            velocity.setX(0);
        } else if (material == Material.SLIME_BLOCK) {
            velocity.setX(Math.copySign(0.25, -velocity.getX()));
        } else {
            velocity.setX(-velocity.getX());
        }
        ball.setVelocity(velocity);
    }

    private void handleYHit(Snowball ball, Vector velocity, Material material) {
        World world = ball.getWorld();
        //  if (material == Material.SOUL_SAND || material == Material.WATER) {
        if (material == Material.SOUL_SAND) {
            handleNonBlockHit(ball, velocity.getX(), velocity.getY(), velocity.getZ());
            return;
        }

        velocity.setY(-velocity.getY());
        velocity.multiply(0.7);

        if (velocity.getY() < 0.1) {
            velocity.setY(0);
            ball.teleport(new Location(world, ball.getLocation().getX(), Math.floor(ball.getLocation().getY() * 2) / 2 + plugin.floorOffset, ball.getLocation().getZ()));
            ball.setGravity(false);
        }
        ball.setVelocity(velocity);
    }
}
