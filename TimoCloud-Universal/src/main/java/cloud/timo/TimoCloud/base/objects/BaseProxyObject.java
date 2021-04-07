package cloud.timo.TimoCloud.base.objects;

import java.util.List;
import java.util.Map;

public class BaseProxyObject {

    private String name;
    private String id;
    private String group;
    private int ram;
    private boolean isStatic;
    private String motd;
    private int maxPlayers;
    private int maxPlayersPerProxy;
    private Map<String, Object> templateHash;
    private Map<String, Object> globalHash;
    private List<String> javaParameters;
    private String jrePath;

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

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public int getRam() {
        return ram;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getMotd() {
        return motd;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMaxPlayersPerProxy() {
        return maxPlayersPerProxy;
    }

    public Map<String, Object> getTemplateHash() {
        return templateHash;
    }

    public Map<String, Object> getGlobalHash() {
        return globalHash;
    }

    public List<String> getJavaParameters() {
        return javaParameters;
    }

    public String getJrePath() {
        return jrePath;
    }
}
