package at.TimoCraft.TimoCloud.bungeecord.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Timo on 28.12.16.
 */
public class TimeUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss", Locale.GERMAN);

    public static String formatTime() {
        return simpleDateFormat.format(new Date());
    }
}
