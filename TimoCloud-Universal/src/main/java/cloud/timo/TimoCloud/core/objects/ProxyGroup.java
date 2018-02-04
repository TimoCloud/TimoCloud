package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import org.json.simple.JSONObject;

import java.util.*;

public class ProxyGroup implements Group {

    private String name;
    private int playersPerProxy;
    private int maxPlayers;
    private int keepFreeSlots;
    private int maxAmount;
    private int ram;
    private String motd;
    private boolean isStatic;
    private int priority;
    private Collection<ServerGroup> serverGroups;
    private String baseName;
    private List<Proxy> proxies = new ArrayList<>();
    private List<Server> registeredServers = new ArrayList<>();

    public ProxyGroup(String name, int playersPerProxy, int maxPlayers, int keepFreeSlots, int maxAmount, int ram, String motd, boolean isStatic, int priority, List<String> serverGroups, String baseName) {
        construct(name, playersPerProxy, maxPlayers, keepFreeSlots, maxAmount, ram, motd, isStatic, priority, serverGroups, baseName);
    }

    public ProxyGroup(JSONObject jsonObject) {
        construct(jsonObject);
    }

    public void construct(JSONObject jsonObject) {
        try {
            construct(
                    (String) jsonObject.get("name"),
                    ((Long) jsonObject.getOrDefault("players-per-proxy", 500)).intValue(),
                    ((Long) jsonObject.getOrDefault("max-players", 100)).intValue(),
                    ((Long) jsonObject.getOrDefault("keep-free-slots", 100)).intValue(),
                    ((Long) jsonObject.getOrDefault("max-amount", 1)).intValue(),
                    ((Long) jsonObject.getOrDefault("ram", 1)).intValue(),
                    (String) jsonObject.getOrDefault("motd", "&6This is a &bTimo&7Cloud &6Proxy\n&aChange this MOTD in your config or per command"),
                    (Boolean) jsonObject.getOrDefault("static", false),
                    ((Long) jsonObject.getOrDefault("priority", 1)).intValue(),
                    (List<String>) jsonObject.getOrDefault("serverGroups", Collections.singletonList("*")),
                    (String) jsonObject.getOrDefault("base", null));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + (String) jsonObject.get("name") + "':");
            e.printStackTrace();
        }
    }

    public void construct(String name, int playersPerProxy, int maxPlayers, int keepFreeSlots, int maxAmount, int ram, String motd, boolean isStatic, int priority, List<String> serverGroups, String baseName) {
        this.name = name;
        this.playersPerProxy = playersPerProxy;
        this.maxPlayers = maxPlayers;
        this.keepFreeSlots = keepFreeSlots;
        this.maxAmount = maxAmount;
        this.ram = ram;
        this.motd = motd;
        this.isStatic = isStatic;
        this.priority = priority;
        serverGroups = new ArrayList<>();
        for (String groupName : serverGroups) {
            groupName = groupName.trim();
            if (groupName.equals("*")) {
                this.serverGroups = TimoCloudCore.getInstance().getServerManager().getServerGroups();
                break;
            }
            this.serverGroups.add(TimoCloudCore.getInstance().getServerManager().getServerGroupByName(groupName));
        }
        this.baseName = baseName;
        if (isStatic() && getBaseName() == null) {
            TimoCloudCore.getInstance().severe("Static proxy group " + getName() + " has no base specified. Please specify a base name in order to get the group started.");
        }
    }

    public JSONObject toJsonObject() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", getName());
        properties.put("players-per-proxy", getPlayersPerProxy());
        properties.put("max-players", getMaxPlayers());
        properties.put("max-amount", getMaxAmount());
        properties.put("keep-free-slots", getKeepFreeSlots());
        properties.put("ram", getRam());
        properties.put("motd", getMotd());
        properties.put("static", isStatic());
        properties.put("priority", getPriority());
        properties.putIfAbsent("serverGroups", getServerGroups());
        if (getBaseName() != null) properties.put("base", getBaseName());
        return new JSONObject(properties);
    }

    public void addStartingProxy(Proxy proxy) {
        if (proxy == null) TimoCloudCore.getInstance().severe("Fatal error: Tried to add proxy which is null. Please report this.");
        if (proxies.contains(proxy)) return;
        proxies.add(proxy);
    }

    public void removeProxy(Proxy proxy) {
        if (getProxies().contains(proxy)) getProxies().remove(proxy);
    }

    public void onProxyConnect(Proxy proxy) {

    }

    public void registerServer(Server server) {
        registeredServers.add(server);
        for (Proxy proxy : getProxies()) proxy.registerServer(server);
    }

    public void unregisterServer(Server server) {
        if (registeredServers.contains(server)) registeredServers.remove(server);
        for (Proxy proxy : getProxies()) proxy.unregisterServer(server);
    }

    public void stopAllProxies() {
        List<Proxy> proxies = (ArrayList<Proxy>) ((ArrayList<Proxy>) getProxies()).clone();
        for (Proxy proxy : proxies) proxy.stop();
        this.proxies.removeAll(proxies);
    }

    public String getName() {
        return name;
    }

    @Override
    public GroupType getType() {
        return GroupType.PROXY;
    }

    public int getOnlinePlayerAmount() {
        return getProxies().stream().mapToInt(Proxy::getOnlinePlayerCount).sum();
    }

    public int getPlayersPerProxy() {
        return playersPerProxy;
    }

    public void setPlayersPerProxy(int playersPerProxy) {
        this.playersPerProxy = playersPerProxy;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    public void setKeepFreeSlots(int keepFreeSlots) {
        this.keepFreeSlots = keepFreeSlots;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Collection<ServerGroup> getServerGroups() {
        return serverGroups;
    }

    @Override
    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public List<Proxy> getProxies() {
        return proxies;
    }

    public List<Server> getRegisteredServers() {
        return registeredServers;
    }

}
