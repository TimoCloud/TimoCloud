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
    private int priority;
    private String  baseName;
    private List<Proxy> proxies;

    public ProxyGroup(String name, int playersPerProxy, int maxPlayers, int maxAmount, int keepFreeSlots, int ram, String motd, boolean isStatic, int priority, String baseName) {
        construct(name, playersPerProxy, maxPlayers, maxAmount, keepFreeSlots, ram, motd, isStatic, priority, baseName);
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
                    (Boolean) jsonObject.getOrDefault("static", false),
                    (Integer) jsonObject.getOrDefault("priority", 1),
                    (String) jsonObject.getOrDefault("base", null));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + (String) jsonObject.get("name") + "':");
            e.printStackTrace();
        }
    }

    public void construct(String name, int playersPerProxy, int maxPlayers, int maxAmount, int keepFreeSlots, int ram, String motd, boolean isStatic, int priority, String baseName) {
        this.name = name;
        this.playersPerProxy = playersPerProxy;
        this.maxPlayers = maxPlayers;
        this.maxAmount = maxAmount;
        this.keepFreeSlots = keepFreeSlots;
        this.ram = ram;
        this.motd = motd;
        this.isStatic = isStatic;
        this.priority = priority;
        this.baseName = baseName;
        if (isStatic() && getBaseName() == null) {
            TimoCloudCore.getInstance().severe("Static proxy group " + getName() + " has no base specified. Please specify a base name in order to get the group started.");
        }
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
        properties.put("priority", getPriority());
        if (getBaseName() != null) properties.put("base", getBaseName());
        return new JSONObject(properties);
    }

    public void addStartingProxy(Proxy proxy) {
        if (proxy == null) TimoCloudCore.getInstance().severe("Fatal error: Tried to add proxy which is null. Please report this.");
        if (proxies.contains(proxy)) TimoCloudCore.getInstance().severe("Tried to add already existing starting proxy " + proxy + ". Please report this.");
        proxies.add(proxy);
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

    public int getPriority() {
        return priority;
    }

    public String getBaseName() {
        return baseName;
    }

    public List<Proxy> getProxies() {
        return proxies;
    }
}
