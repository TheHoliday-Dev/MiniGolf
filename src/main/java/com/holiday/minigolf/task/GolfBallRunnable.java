package com.holiday.minigolf.task;


import com.holiday.minigolf.Main;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;

public class GolfBallRunnable extends BukkitRunnable {
    private final Snowball golfBall;

    public GolfBallRunnable(Snowball golfBall) {
        this.golfBall = golfBall;
    }

    @Override
    public void run() {
        if (!golfBall.isValid()) {
            Main.getInstance().ballRunnables.remove(golfBall.getUniqueId());
            cancel();
            return;
        }
        golfBall.teleport(golfBall.getLocation());
    }
}
