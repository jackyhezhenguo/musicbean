package com.musicbean.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by boyo on 16/3/13.
 */
public class DateUtils {

    public static String timeMillisToDateString(long timeMillis) {
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return formatter.format(calendar.getTime());
    }

    private static String timeMillisToActiveTime(long timeMillis) {
        timeMillis += 16 * 60 * 60 * 1000;
        DateFormat formatter = new SimpleDateFormat("H:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return formatter.format(calendar.getTime());
    }

    public static String timeSecondsToActiveTime(long time) {
        return timeMillisToActiveTime(time * 1000);
    }

    public static boolean isToday(String time) {
        //String timeStr = timeMillisToDateString(time);
        String timeCur = timeMillisToDateString(System.currentTimeMillis());
        return timeCur.equals(time);
    }

    public static String getDateStringFromToday(int diff) {
        long time = System.currentTimeMillis();
        long ret = time - diff * (24 * 60 * 60 * 1000);
        DateFormat formatter = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ret);
        return formatter.format(calendar.getTime());
    }

    public static String formatDuration(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }
}
