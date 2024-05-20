package com.badbones69.crazyenvoys.api.objects.misc.v2.records;

import java.util.concurrent.ThreadLocalRandom;

public record ChanceSettings(boolean useChance, int maxRange, int chance) {

    /**
     * A temporary method to test chance percentages. This will likely change a lot as I do more research
     * I want to simulate as random as possible and potentially include a weight system.
     *
     * @return true or false
     */
    public boolean random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int index = 0; index < 100; index++) {
            final int number = random.nextInt(maxRange);

            if (number >= 1 && number <= chance) {
                return true;
            }
        }

        return false;
    }
}