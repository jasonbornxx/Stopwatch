package com.sleekstopwatch.app;

public class StopwatchUtils {

    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatMillis(long millis) {
        long centis = (millis % 1000) / 10;
        return String.format(".%02d", centis);
    }

    public static String formatFull(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        long centis = (millis % 1000) / 10;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, centis);
        }
        return String.format("%02d:%02d.%02d", minutes, seconds, centis);
    }
}
