package com.badbones69.crazyenvoys.listeners.timer;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.jetbrains.annotations.NotNull;

/**
 * A simple countdown timer using the Runnable interface in seconds!
 *
 * @author ExpDev
 */
public class CountdownTimer implements Runnable {

    @NotNull
    private final CrazyEnvoys plugin = CrazyEnvoys.get();

    // Our scheduled task's assigned id, needed for canceling
    private Integer assignedTaskId;

    // Seconds and shiz
    private final int seconds;
    private int secondsLeft;

    public CountdownTimer(int seconds) {
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
            if (this.assignedTaskId != null) this.plugin.getServer().getScheduler().cancelTask(this.assignedTaskId);

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
        // Initialize our assigned task's id, for later use, so we can cancel.
        this.assignedTaskId = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 0L, 20L);
    }
}