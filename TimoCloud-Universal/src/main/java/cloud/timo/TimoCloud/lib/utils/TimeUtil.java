package cloud.timo.TimoCloud.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.GERMAN);

    public static String formatTime() {
        return simpleDateFormat.format(new Date());
    }
}
