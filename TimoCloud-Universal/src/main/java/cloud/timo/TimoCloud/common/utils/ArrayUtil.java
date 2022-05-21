package cloud.timo.TimoCloud.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayUtil {

    public <T> T[] concatArrays(T[] a, T[] b) {
        final int alen = a.length;
        final int blen = b.length;
        final T[] result = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), alen + blen);
        System.arraycopy(a, 0, result, 0, alen);
        System.arraycopy(b, 0, result, alen, blen);
        return result;
    }

}
