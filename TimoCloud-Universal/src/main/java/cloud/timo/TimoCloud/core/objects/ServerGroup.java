package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.serverGroup.*;
import cloud.timo.TimoCloud.api.internal.links.ServerGroupObjectLink;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ServerGroupObjectCoreImplementation;

import java.util.*;
import java.util.stream.Collectors;

public class ServerGroup implements Group {

    private String id;
    private String name;

    private int onlineAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private int priority;
    private Base base;
    private Set<String> sortOutStates;
    private List<String> javaParameters;
    private List<String> spigotParameters;
    private String jrePath;

    private Map<String, Server> servers = new HashMap<>();

    public ServerGroup(ServerGroupProperties properties) {
        construct(properties);
    }

    public ServerGroup(Map<String, Object> properties) {
        construct(properties);
    }

    public ServerGroup(String id, String name, int onlineAmount, int maxAmount, int ram, boolean isStatic, int priority, String baseName, Collection<String> sortOutStates, List<String> javaParameters, List<String> spigotParameters, String jdkPath) {
        construct(id, name, onlineAmount, maxAmount, ram, isStatic, priority, baseName, sortOutStates, javaParameters, spigotParameters, jdkPath);
    }

    public void construct(Map<String, Object> properties) {
        try {
            String name = (String) properties.get("name");
            ServerGroupProperties defaultProperties = new ServerGroupProperties(name);
            construct(
                    (String) properties.getOrDefault("id", defaultProperties.getId()),
                    name,
                    ((Number) properties.getOrDefault("online-amount", defaultProperties.getOnlineAmount())).intValue(),
                    ((Number) properties.getOrDefault("max-amount", defaultProperties.getMaxAmount())).intValue(),
                    ((Number) properties.getOrDefault("ram", defaultProperties.getRam())).intValue(),
                    (Boolean) properties.getOrDefault("static", defaultProperties.isStatic()),
                    ((Number) properties.getOrDefault("priority", defaultProperties.getPriority())).intValue(),
                    (String) properties.getOrDefault("base", defaultProperties.getBaseIdentifier()),
                    (Collection<String>) properties.getOrDefault("sort-out-states", defaultProperties.getSortOutStates()),
                    (List<String>) properties.getOrDefault("javaParameters", defaultProperties.getJavaParameters()),
                    (List<String>) properties.getOrDefault("spigotParameters", defaultProperties.getSpigotParameters()),
                    ((String) properties.getOrDefault("jrePath", defaultProperties.getJrePath())));
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
        if (getBase() != null) properties.put("base", getBase().getId());
        properties.put("sort-out-states", getSortOutStates());
        properties.put("javaParameters", getJavaParameters());
        properties.put("spigotParameters", getSpigotParameters());
        properties.put("jrePath", getJrePath());
        return properties;
    }

    public void construct(ServerGroupProperties properties) {
        construct(properties.getId(), properties.getName(), properties.getOnlineAmount(), properties.getMaxAmount(), properties.getRam(), properties.isStatic(), properties.getPriority(), properties.getBaseIdentifier(), properties.getSortOutStates(), properties.getJavaParameters(), properties.getSpigotParameters(), properties.getJrePath());
    }

    public void construct(String id, String name, int onlineAmount, int maxAmount, int ram, boolean isStatic, int priority, String baseIdentifier, Collection<String> sortOutStates, List<String> javaParameters, List<String> spigotParameters, String jrePath) {
        if (isStatic() && onlineAmount > 1) {
            TimoCloudCore.getInstance().severe("Static groups (" + name + ") can only have 1 server. Please set 'onlineAmount' to 1");
            onlineAmount = 1;
        }
        this.id = id;
        this.name = name;
        this.onlineAmount = onlineAmount;
        this.maxAmount = maxAmount;
        this.ram = ram;
        this.isStatic = isStatic;
        this.priority = priority;
        if (baseIdentifier != null)
            this.base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseIdentifier);
        this.sortOutStates = new HashSet<>(sortOutStates);
        this.spigotParameters = spigotParameters;
        this.javaParameters = javaParameters;
        this.jrePath = jrePath;
        if (isStatic() && getBase() == null) {
            TimoCloudCore.getInstance().severe("Static server group " + getName() + " has no base specified. Please specify a base name in order to enable starting of servers.");
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
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
        for (Server server : getServers()) {
            server.stop();
            removeServer(server);
        }
    }

    public void onServerConnect(Server server) {

    }

    public void addServer(Server server) {
        if (server == null) {
            TimoCloudCore.getInstance().severe("Fatal error: Tried to add server which is null. Please report this.");
            return;
        }
        servers.put(server.getId(), server);
        TimoCloudCore.getInstance().getInstanceManager().addServer(server);
    }

    public void removeServer(Server server) {
        servers.remove(server.getId());
        TimoCloudCore.getInstance().getInstanceManager().removeServer(server);
    }

    public Collection<Server> getServers() {
        return new HashSet<>(servers.values());
    }

    public Server getServerById(String id) {
        return servers.get(id);
    }

    public void setOnlineAmount(int onlineAmount) {
        int oldValue = getOnlineAmount();
        this.onlineAmount = onlineAmount;
        EventTransmitter.sendEvent(new ServerGroupOnlineAmountChangeEventBasicImplementation(toGroupObject(), oldValue, onlineAmount));
    }

    public void setMaxAmount(int maxAmount) {
        int oldValue = getMaxAmount();
        this.maxAmount = maxAmount;
        EventTransmitter.sendEvent(new ServerGroupMaxAmountChangeEventBasicImplementation(toGroupObject(), oldValue, maxAmount));
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getRam() {
        if (ram < 128) {
            ram *= 1024;
        }
        return ram;
    }

    public void setRam(int ram) {
        int oldValue = getRam();
        this.ram = ram;
        EventTransmitter.sendEvent(new ServerGroupRamChangeEventBasicImplementation(toGroupObject(), oldValue, ram));
    }

    public void setStatic(boolean isStatic) {
        Boolean oldValue = isStatic();
        this.isStatic = isStatic;
        EventTransmitter.sendEvent(new ServerGroupStaticChangeEventBasicImplementation(toGroupObject(), oldValue, isStatic));
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        int oldValue = getPriority();
        this.priority = priority;
        EventTransmitter.sendEvent(new ServerGroupPriorityChangeEventBasicImplementation(toGroupObject(), oldValue, priority));
    }

    public List<String> getJavaParameters() {
        return javaParameters;
    }

    public void setJavaParameters(List<String> javaParameters) {
        List<String> oldValue = getJavaParameters();
        this.javaParameters = new ArrayList<>(javaParameters);
        EventTransmitter.sendEvent(new ServerGroupJavaParametersChangeEventBasicImplementation(toGroupObject(), oldValue, javaParameters));
    }

    public List<String> getSpigotParameters() {
        return spigotParameters;
    }

    public void setSpigotParameters(List<String> spigotParameters) {
        List<String> oldValue = getSpigotParameters();
        this.spigotParameters = new ArrayList<>(spigotParameters);
        EventTransmitter.sendEvent(new ServerGroupSpigotParametersChangeEventBasicImplementation(toGroupObject(), oldValue, spigotParameters));
    }

    public String getJrePath() {
        return jrePath;
    }

    public void setJrePath(String jrePath) {
        this.jrePath = jrePath;
    }

    public void setBase(Base base) {
        Base oldValue = getBase();
        this.base = base;
        EventTransmitter.sendEvent(new ServerGroupBaseChangeEventBasicImplementation(toGroupObject(), oldValue.toBaseObject(), base.toBaseObject()));
    }

    @Override
    public Base getBase() {
        return base;
    }

    public Set<String> getSortOutStates() {
        return sortOutStates;
    }

    public void setSortOutStates(Collection<String> sortOutStates) {
        this.sortOutStates = new HashSet<>(sortOutStates);
    }

    public ServerGroupObject toGroupObject() {
        return new ServerGroupObjectCoreImplementation(
                getId(),
                getName(),
                getServers().stream().map(Server::toLink).collect(Collectors.toSet()),
                getOnlineAmount(),
                getMaxAmount(),
                getRam(),
                isStatic(),
                getBase() == null ? null : getBase().toLink(),
                getPriority(),
                getSortOutStates(),
                getJavaParameters(),
                getSpigotParameters()
        );
    }

    public ServerGroupObjectLink toLink() {
        return new ServerGroupObjectLink(getId(), getName());
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
