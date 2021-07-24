package cloud.timo.TimoCloud.base.objects;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class BaseProxyObject {

    private final String name;
    private final String id;
    private final String group;
    private final int ram;
    private final boolean isStatic;
    private final String motd;
    private final int maxPlayers;
    private final int maxPlayersPerProxy;
    private final Map<String, Object> templateHash;
    private final Map<String, Object> globalHash;
    private final List<String> javaParameters;
    private final String jrePath;

    public BaseProxyObject(String name, String id, int ram, boolean isStatic, String group, String motd, int maxPlayers, int maxPlayersPerProxy, Map<String, Object> templateHash, Map<String, Object> globalHash, List<String> javaParameters, String jrePath) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.ram = ram;
        this.isStatic = isStatic;
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.maxPlayersPerProxy = maxPlayersPerProxy;
        this.templateHash = templateHash;
        this.globalHash = globalHash;
        this.javaParameters = javaParameters;
        this.jrePath = jrePath;
    }
}
