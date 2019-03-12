package cloud.timo.TimoCloud.common.datatypes;

import java.util.HashMap;
import java.util.Map;

public class TypeMap extends HashMap {

    public TypeMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TypeMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TypeMap() {
    }

    public TypeMap(Map m) {
        super(m);
    }

    public Boolean getBoolean(String key) {
        Object object = get(key);
        if (object == null) return null;
        return (Boolean) object;
    }

    public Integer getInteger(String key) {
        Object object = get(key);
        if (object == null) return null;
        return ((Number) object).intValue();
    }

    public Long getLong(String key) {
        Object object = get(key);
        if (object == null) return null;
        return ((Number) object).longValue();
    }

    public Short getShort(String key) {
        Object object = get(key);
        if (object == null) return null;
        return ((Number) object).shortValue();
    }

    public Double getDouble(String key) {
        Object object = get(key);
        if (object == null) return null;
        return ((Number) object).doubleValue();
    }

    public Float getFloat(String key) {
        Object object = get(key);
        if (object == null) return null;
        return ((Number) object).floatValue();
    }

    public String getString(String key) {
        return (String) get(key);
    }

    @Override
    public TypeMap put(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public TypeMap putIfCondition(Object key, Object value, boolean condition) {
        if (! condition) return this;
        return put(key, value);
    }

    public TypeMap putIfNotNull(Object key, Object value) {
        return putIfCondition(key, value, value != null);
    }

    public TypeMap putIfAbsent(String key, Object value) {
        return putIfCondition(key, value, ! containsKey(key));
    }
    
}
