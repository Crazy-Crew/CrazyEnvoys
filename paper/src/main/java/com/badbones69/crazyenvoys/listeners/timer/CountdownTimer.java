package com.badbones69.crazyenvoys.listeners.timer;

import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A simple countdown timer using the Runnable interface in seconds!
 *
 * @author ExpDev
 */
public class CountdownTimer extends FoliaScheduler {

    // Seconds and shiz
    private final int seconds;
    private int secondsLeft;

    public CountdownTimer(final JavaPlugin plugin, final int seconds) {
        super(plugin, Scheduler.global_scheduler);

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
        runAtFixedRate(0L, 20L);
    }
}