package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ServerGroupObjectCoreImplementation;

import java.util.*;
import java.util.stream.Collectors;

public class ServerGroup implements Group {

    private String name;
    private List<Server> servers = new ArrayList<>();
    private int onlineAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private int priority;
    private String baseName;
    private List<String> sortOutStates;

    public ServerGroup() {
    }

    public ServerGroup(Map<String, Object> properties) {
        construct(properties);
    }

    public ServerGroup(String name, int onlineAmount, int maxAmount, int ram, boolean isStatic, int priority, String baseName, List<String> sortOutStates) {
        construct(name, onlineAmount, maxAmount, ram, isStatic, priority, baseName, sortOutStates);
    }

    public void construct(Map<String, Object> properties) {
        try {
            construct(
                    (String) properties.get("name"),
                    ((Number) properties.getOrDefault("online-amount", 1)).intValue(),
                    ((Number) properties.getOrDefault("max-amount", 10)).intValue(),
                    ((Number) properties.getOrDefault("ram", 1024)).intValue(),
                    (Boolean) properties.getOrDefault("static", false),
                    ((Number) properties.getOrDefault("priority", 1)).intValue(),
                    (String) properties.getOrDefault("base", null),
                    (List<String>) properties.getOrDefault("sort-out-states", Arrays.asList("OFFLINE", "STARTING", "INGAME", "RESTARTING")));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + properties.get("name") + "':");
            e.printStackTrace();
        }
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", getName());
        properties.put("online-amount", getOnlineAmount());
        properties.put("max-amount", getMaxAmount());
        properties.put("ram", getRam());
        properties.put("static", isStatic());
        properties.put("priority", getPriority());
        if (getBaseName() != null) properties.put("base", getBaseName());
        properties.put("sort-out-states", getSortOutStates());
        return properties;
    }

    public void construct(String name, int startupAmount, int maxAmount, int ram, boolean isStatic, int priority, String baseName, List<String> sortOutStates) {
        if (isStatic() && startupAmount > 1) {
            TimoCloudCore.getInstance().severe("Static groups (" + name + ") can only have 1 server. Please set 'onlineAmount' to 1");
            startupAmount = 1;
        }
        this.name = name;
        setOnlineAmount(startupAmount);
        setMaxAmount(maxAmount);
        if (ram <128) ram*=1024;
        setRam(ram);
        setStatic(isStatic);
        setBaseName(baseName);
        setSortOutStates(sortOutStates);
        if (isStatic() && getBaseName() == null) {
            TimoCloudCore.getInstance().severe("Static server group " + getName() + " has no base specified. Please specify a base name in order to get the group started.");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public GroupType getType() {
        return GroupType.SERVER;
    }

    public int getOnlineAmount() {
        return onlineAmount;
    }

    public void stopAllServers() {
        List<Server> servers = (ArrayList<Server>) ((ArrayList<Server>) getServers()).clone();
        for (Server server : servers) server.stop();
        this.servers.removeAll(servers);
    }

    public void onServerConnect(Server server) {

    }

    public void addStartingServer(Server server) {
        if (server == null) TimoCloudCore.getInstance().severe("Fatal error: Tried to add server which is null. Please report this.");
        if (servers.contains(server)) return;
        servers.add(server);
    }

    public void removeServer(Server server) {
        if (servers.contains(server)) servers.remove(server);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setOnlineAmount(int onlineAmount) {
        this.onlineAmount = onlineAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        if (ram < 128) TimoCloudCore.getInstance().severe("Attention: ServerGroup " + name + " has less than 128MB Ram. (This won't work)");
        this.ram = ram;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getBaseName() {
        return baseName;
    }

    public List<String> getSortOutStates() {
        return sortOutStates;
    }

    public void setSortOutStates(List<String> sortOutStates) {
        this.sortOutStates = sortOutStates;
    }

    public ServerGroupObject toGroupObject() {
        ServerGroupObjectCoreImplementation groupObject = new ServerGroupObjectCoreImplementation(
                getName(),
                getServers().stream().map(Server::toServerObject).collect(Collectors.toList()),
                getOnlineAmount(),
                getMaxAmount(),
                getRam(),
                isStatic(),
                getBaseName(),
                getSortOutStates()
        );
        Collections.sort((List<ServerObjectBasicImplementation>) (List) groupObject.getServers());
        return groupObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerGroup that = (ServerGroup) o;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

}
