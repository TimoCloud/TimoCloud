package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import org.json.simple.JSONObject;

import java.util.*;

public class ProxyGroup implements Group {

    private String name;
    private int playersPerProxy;
    private int maxPlayers;
    private int maxAmount;
    int keepFreeSlots;
    private int ram;
    private String motd;
    private boolean isStatic;
    private String  baseName;
    private List<Proxy> proxies;

    public ProxyGroup(String name, int playersPerProxy, int maxPlayers, int maxAmount, int keepFreeSlots, int ram, String motd, boolean isStatic, String baseName) {
        construct(name, playersPerProxy, maxPlayers, maxAmount, keepFreeSlots, ram, motd, isStatic, baseName);
    }

    public ProxyGroup(JSONObject jsonObject) {
        construct(jsonObject);
    }

    public void construct(JSONObject jsonObject) {
        try {
            construct(
                    (String) jsonObject.get("name"),
                    (Integer) jsonObject.getOrDefault("players-per-proxy", 500),
                    (Integer) jsonObject.getOrDefault("max-players", 100),
                    (Integer) jsonObject.getOrDefault("max-amount", 1),
                    (Integer) jsonObject.getOrDefault("keep-free-slots", 100),
                    (Integer) jsonObject.getOrDefault("ram", 1),
                    (String) jsonObject.getOrDefault("motd", "&6This is a &bTimo&7Cloud &6Proxy\n&aChange this MOTD in your config or per command"),
                    (Boolean) jsonObject.getOrDefault("static", true),
                    (String) jsonObject.getOrDefault("base", "BASE-1"));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + (String) jsonObject.get("name") + "':");
            e.printStackTrace();
        }
    }

    public void construct(String name, int playersPerProxy, int maxPlayers, int maxAmount, int keepFreeSlots, int ram, String motd, boolean isStatic, String baseName) {
        this.name = name;
        this.playersPerProxy = playersPerProxy;
        this.maxPlayers = maxPlayers;
        this.maxAmount = maxAmount;
        this.keepFreeSlots = keepFreeSlots;
        this.ram = ram;
        this.motd = motd;
        this.isStatic = isStatic;
        this.baseName = baseName;
        this.proxies = new ArrayList<>();
    }

    public JSONObject toJsonObject() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", getName());
        properties.put("players-per-proxy", getPlayersPerProxy());
        properties.put("max-players", getMaxPlayers());
        properties.put("max-amount", getMaxAmount());
        properties.put("keep-free-slots", getKeepFreeSlots());
        properties.put("ram", getRam());
        properties.put("motd", isStatic());
        properties.put("static", isStatic());
        if (getBaseName() != null) properties.put("base", getBaseName());
        return new JSONObject(properties);
    }

    public String getName() {
        return name;
    }

    public int getPlayersPerProxy() {
        return playersPerProxy;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    public int getRam() {
        return ram;
    }

    public String getMotd() {
        return motd;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getBaseName() {
        return baseName;
    }

    public List<Proxy> getProxies() {
        return proxies;
    }

    public void addProxy(Proxy proxy) {
        if (proxies.contains(proxy)) return;
        proxies.add(proxy);
    }
}
