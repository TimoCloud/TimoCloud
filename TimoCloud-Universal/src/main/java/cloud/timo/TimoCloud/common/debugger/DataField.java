package cloud.timo.TimoCloud.common.debugger;

import cloud.timo.TimoCloud.common.protocol.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DataField {

    @Getter
    private final String id;
    @Getter
    private final Class<?> fieldClass;
    @Getter
    private final Object fieldData;

    private static String getClassName(Class<?> clazz) {
        if (clazz == null) return "null";
        if (clazz == char[].class) return getClassName(String.class);
        return clazz.getName();
    }

    public Message toJson() {
        return Message.create()
                .set("id", getId())
                .set("class", getClassName(getFieldClass()))
                .set("data", getFieldData());
    }
}
