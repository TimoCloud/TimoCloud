package cloud.timo.TimoCloud.base.objects;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class BaseServerObject {

    private final String name;
    private final String id;
    private final String group;
    private final int ram;
    private final boolean isStatic;
    private final String map;
    private final Map<String, Object> templateHash;
    private final Map<String, Object> mapHash;
    private final Map<String, Object> globalHash;
    private final List<String> javaParameters;
    private final List<String> spigotParameters;
    private final String jrePath;

    public BaseServerObject(String name, String id, int ram, boolean isStatic, String map, String group, Map<String, Object> templateHash, Map<String, Object> mapHash, Map<String, Object> globalHash, List<String> javaParameters, List<String> spigotParameters, String jrePath) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.ram = ram;
        this.isStatic = isStatic;
        this.map = map;
        this.templateHash = templateHash;
        this.mapHash = mapHash;
        this.globalHash = globalHash;
        this.javaParameters = javaParameters;
        this.spigotParameters = spigotParameters;
        this.jrePath = jrePath;
    }
}
