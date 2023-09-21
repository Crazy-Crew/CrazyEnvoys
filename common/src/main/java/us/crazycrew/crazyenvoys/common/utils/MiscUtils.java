package us.crazycrew.crazyenvoys.common.utils;

import java.util.List;

public class MiscUtils {

    /**
     * Loops through a string-list and does magic with \n then returns the string builder
     *
     * @param list to convert
     * @return the string-builder
     */
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();

        for (String line : list) {
            message.append(line).append("\n");
        }

        return message.toString();
    }
}