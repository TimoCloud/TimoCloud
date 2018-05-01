package cloud.timo.TimoCloud.lib.debugger;

import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.simple.JSONObject;

@AllArgsConstructor
public class DataField {
    @Getter
    private String id;
    @Getter
    private Class fieldClass;
    @Getter
    private Object fieldData;

    public JSONObject toJson() {
        return JSONBuilder.create()
                .set("id", getId())
                .set("class", getClassName(getFieldClass()))
                .set("data", getFieldData())
                .toJson();
    }

    private static String getClassName(Class clazz) {
        if (clazz == null) return "null";
        if (clazz == char[].class) return getClassName(String.class);
        return clazz.getName();
    }
}
