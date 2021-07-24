package cloud.timo.TimoCloud.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnumUtil {

    public <E extends Enum<E>> E valueOf(Class<E> e, String name) {
        if (name == null) {
            return null;
        }

        try {
            return Enum.valueOf(e, name.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
