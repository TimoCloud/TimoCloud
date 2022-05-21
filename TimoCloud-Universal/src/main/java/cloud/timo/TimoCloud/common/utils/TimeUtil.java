package cloud.timo.TimoCloud.common.utils;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@UtilityClass
public class TimeUtil {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.GERMAN);

    public String formatTime() {
        return simpleDateFormat.format(new Date());
    }
}
