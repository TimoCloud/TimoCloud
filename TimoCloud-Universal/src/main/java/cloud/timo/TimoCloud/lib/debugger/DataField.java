package cloud.timo.TimoCloud.lib.debugger;

import cloud.timo.TimoCloud.lib.protocol.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DataField {
    @Getter
    private String id;
    @Getter
    private Class fieldClass;
    @Getter
    private Object fieldData;

    public Message toJson() {
        return Message.create()
                .set("id", getId())
                .set("class", getClassName(getFieldClass()))
                .set("data", getFieldData());
    }

    private static String getClassName(Class clazz) {
        if (clazz == null) return "null";
        if (clazz == char[].class) return getClassName(String.class);
        return clazz.getName();
    }
}
