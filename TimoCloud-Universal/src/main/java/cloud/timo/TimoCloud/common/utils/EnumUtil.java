package cloud.timo.TimoCloud.common.utils;

public class EnumUtil {
    public static <E extends Enum<E>> E valueOf(Class<E> e, String name) {
        if (name != null) name = name.toUpperCase();
        try {
            return Enum.valueOf(e, name);
        } catch (Exception ex) {
            return null;
        }
    }
}
