package cloud.timo.TimoCloud.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Assert {

    public void notNull(Object object) throws AssertionError {
        if (object == null) throw new AssertionError("Object is null");
    }

    public void instanceOf(Object object, Class<?> clazz) throws AssertionError {
        if (!clazz.isInstance(object)) throw new AssertionError("Object is not instance of " + clazz.getName());
    }

    public void isTrue(Boolean b) throws AssertionError {
        if (!b) throw new AssertionError("Boolean is not true");
    }

    public void equals(Object a, Object b) throws AssertionError {
        if (a == null && b == null) return;
        if (a == null) throw new AssertionError("Object a is null");
        if (b == null) throw new AssertionError("Object b is null");
        if (!a.equals(b)) throw new AssertionError("Objects are not equal");
    }
}
