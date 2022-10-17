package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.CrazyEnvoys;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple countdown timer using the Runnable interface in seconds!
 * <b>Great for minigames and other shiz?</b>
 * <p>
 * Project created by
 * @author ExpDev
 */
public class CountdownTimer implements Runnable {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    // Our scheduled task's assigned id, needed for canceling
    private Integer assignedTaskId;

    // Seconds and shiz
    private final int seconds;
    private int secondsLeft;

    // Actions to perform while counting down, before and after.

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
        if (secondsLeft < 1) {
            // Do what was supposed to happen after the timer.
            // afterTimer.run();

            // Cancel timer.
            if (assignedTaskId != null) plugin.getServer().getScheduler().cancelTask(assignedTaskId);

            return;
        }

        // Are we just starting?
        // if (secondsLeft == seconds) beforeTimer.run();

        // Do what's supposed to happen every second.
        // everySecond.accept(this);

        // Decrement the seconds left.
        secondsLeft--;
    }

    /**
     * Gets the total seconds this timer was set to run for.
     *
     * @return Total seconds timer should run.
     */
    public int getTotalSeconds() {
        return seconds;
    }

    /**
     * Gets the seconds left this timer should run.
     *
     * @return Seconds left timer should run.
     */
    public int getSecondsLeft() {
        return secondsLeft;
    }

    /**
     * Schedules this instance to "run" every second.
     */
    public void scheduleTimer() {
        // Initialize our assigned task's id, for later use, so we can cancel.
        this.assignedTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }

    public boolean isActiveSchedule() {
        return plugin.getServer().getScheduler().isCurrentlyRunning(assignedTaskId);
    }
}