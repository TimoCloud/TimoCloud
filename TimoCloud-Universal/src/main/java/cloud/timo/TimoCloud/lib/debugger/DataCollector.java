package cloud.timo.TimoCloud.lib.debugger;

import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class DataCollector {

    private static final List<String> HIDDEN_KEYS = Arrays.asList("api-key");

    public static JSONObject collectData(Object root) throws IllegalAccessException {
        return collectData(root, new HashMap<>(), root.getClass().getName());
    }

    public static JSONObject collectData(Object root, Map<Object, String> used, String rootId) throws RuntimeException {
        Map json = new LinkedHashMap();
        for (Field field : root.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) continue;
            Object object = null;
            try {
                object = field.get(root);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String id = object == null
                    ? "null"
                    : System.identityHashCode(object) + "";
            if (used.containsKey(id)) {
                return JSONBuilder.create()
                        .set("link", used.get(id))
                        .toJson();
            }
            used.put(id, id);
            Object data = null;
            String newId = rootId + "." + field.getName();
            if (object != null) {
                if (getPackageName(object.getClass()).startsWith("cloud.timo")) {
                    data = collectData(object, used, newId);
                } else if (object instanceof Collection) {
                    JSONArray array = new JSONArray();
                    for (Object o : (Collection) object) {
                        array.add(collectData(o, used, newId + "." + o == null ? "null" : o.toString()));
                    }
                    data = array;
                } else if (object instanceof Map) {
                    final Map map = (Map) object;
                    data = map.keySet().stream().map(key -> JSONBuilder.create()
                            .set("key", key.toString())
                            .setIfCondition("value", collectData(map.get(key), used, newId + "." + key), ! HIDDEN_KEYS.contains(key))
                            .setIfCondition("hidden", true, HIDDEN_KEYS.contains(key))
                    ).collect(Collectors.toList());
                } else if (object instanceof char[]) {
                    data = new String((char[]) object);
                } else {
                    data = object.toString();
                }
            }
            DataField dataField = new DataField(id, field.getType(), data);
            json.put(field.getName(), dataField.toJson());
        }
        return new JSONObject(json);
    }

    private static String getPackageName(Class clazz) {
        if (clazz == null) return "null";
        if (clazz.isPrimitive() || clazz.getPackage() == null) {
            return clazz.getTypeName();
        }
        return clazz.getPackage().getName();
    }
}
