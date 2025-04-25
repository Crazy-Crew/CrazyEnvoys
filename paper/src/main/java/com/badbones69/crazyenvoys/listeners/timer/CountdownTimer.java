package com.badbones69.crazyenvoys.listeners.timer;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * A simple countdown timer using the Runnable interface in seconds!
 *
 * @author ExpDev
 */
public class CountdownTimer extends FoliaRunnable {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    // Seconds and shiz
    private final int seconds;
    private int secondsLeft;

    public CountdownTimer(JavaPlugin plugin, int seconds) {
        super(plugin.getServer().getGlobalRegionScheduler());

        this.seconds = seconds;
        this.secondsLeft = seconds;
    }

    /**
     * Runs the timer once, decrements seconds etc...
     * Really wish we could make it protected/private, so you couldn't access it.
     */
    @Override
    public void run() {
        // Is the timer up?
        if (this.secondsLeft < 1) {
            cancel();

            return;
        }

        // Decrement the seconds left.
        this.secondsLeft--;
    }

    /**
     * Gets the total seconds this timer was set to run for.
     *
     * @return Total seconds timer should run.
     */
    public int getTotalSeconds() {
        return this.seconds;
    }

    /**
     * Gets the seconds left this timer should run.
     *
     * @return Seconds left timer should run.
     */
    public int getSecondsLeft() {
        return this.secondsLeft;
    }

    /**
     * Schedules this instance to "run" every second.
     */
    public void scheduleTimer() {
        runAtFixedRate(this.plugin, 0L, 20L);
    }
}