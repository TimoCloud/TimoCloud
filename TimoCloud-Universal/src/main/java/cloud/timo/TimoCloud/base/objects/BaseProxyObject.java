package cloud.timo.TimoCloud.base.objects;

import org.json.simple.JSONObject;

public class BaseProxyObject {
    private String name;
    private String group;
    private int ram;
    private boolean isStatic;
    private String token;
    private String motd;
    private int maxPlayers;
    private int maxPlayersPerProxy;
    private JSONObject templateHash;
    private JSONObject globalHash;

    public BaseProxyObject(String name, String group, int ram, boolean isStatic, String token, String motd, int maxPlayers, int maxPlayersPerProxy, JSONObject templateHash, JSONObject globalHash) {
        this.name = name;
        this.group = group;
        this.ram = ram;
        this.isStatic = isStatic;
        this.token = token;
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.maxPlayersPerProxy = maxPlayersPerProxy;
        this.templateHash = templateHash;
        this.globalHash = globalHash;
    }

    public String getName() {
        return name;
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

    public String getToken() {
        return token;
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

    public JSONObject getTemplateHash() {
        return templateHash;
    }

    public JSONObject getGlobalHash() {
        return globalHash;
    }
}
