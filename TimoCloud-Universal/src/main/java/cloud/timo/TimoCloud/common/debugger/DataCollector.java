package cloud.timo.TimoCloud.common.debugger;

import cloud.timo.TimoCloud.common.protocol.Message;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class DataCollector {

    private final Set<String> HIDDEN_KEYS = new HashSet<>(Collections.singletonList("api-key"));

    public Message collectData(Object root) {
        return collectData(root, new HashMap<>(), root.getClass().getName());
    }

    public Message collectData(Object root, Map<Object, String> used, String rootId) throws RuntimeException {
        Message json = Message.create();
        for (Field field : root.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) continue;
            Object object;
            try {
                object = field.get(root);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String id = object == null
                    ? "null"
                    : System.identityHashCode(object) + "";
            if (used.containsKey(id)) {
                return Message.create()
                        .set("link", used.get(id));
            }

            used.put(id, id);
            Object data = null;
            String newId = rootId + "." + field.getName();
            if (object != null) {
                if (getPackageName(object.getClass()).startsWith("cloud.timo")) {
                    data = collectData(object, used, newId);
                } else if (object instanceof Collection) {
                    List array = new ArrayList();
                    for (Object o : (Collection) object) {
                        array.add(collectData(o, used, newId + "." + o == null ? "null" : o.toString()));
                    }
                    data = array;
                } else if (object instanceof Map) {
                    final Map map = (Map) object;
                    data = map.keySet().stream().map(key -> Message.create()
                            .set("key", key == null ? null : key.toString())
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
        return json;
    }

    private String getPackageName(Class clazz) {
        if (clazz == null) return "null";
        if (clazz.isPrimitive() || clazz.getPackage() == null) {
            return clazz.getTypeName();
        }
        return clazz.getPackage().getName();
    }
}
