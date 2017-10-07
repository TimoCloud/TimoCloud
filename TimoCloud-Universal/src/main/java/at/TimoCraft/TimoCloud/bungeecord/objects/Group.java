package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Group {

    private String name;
    private List<Server> servers = new ArrayList<>();
    private int startupAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private String baseName;
    private BaseObject base;
    private List<String> sortOutStates;

    public Group() {
    }

    public Group(String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String baseName, List<String> sortOutStates) {
        construct(name, startupAmount, maxAmount, ram, isStatic, baseName, sortOutStates);
    }

    public void construct(String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String baseName, List<String> sortOutStates) {
        if (isStatic() && startupAmount > 1) {
            TimoCloud.severe("Static groups (" + name + ") can only have 1 server. Please set 'onlineAmount' to 1");
            return;
        }
        this.name = name;
        setStartupAmount(startupAmount);
        setMaxAmount(maxAmount);
        setRam(ram);
        setStatic(isStatic);
        setBaseName(baseName);
        setBase(TimoCloud.getInstance().getServerManager().getBase(baseName));
        setSortOutStates(sortOutStates);
    }

    public String getName() {
        return name;
    }

    public int getStartupAmount() {
        return startupAmount;
    }

    public void stopAllServers() {
        List<Server> servers = (ArrayList<Server>) ((ArrayList) getServers()).clone();
        for (Server server : servers) server.stop();
        this.servers.removeAll(servers);
        if (! this.servers.isEmpty()) stopAllServers();
    }

    public void onServerConnect(Server server) {

    }

    public void addStartingServer(Server server) {
        if (server == null) TimoCloud.severe("Fatal error: Tried to add server which is null. Please report this.");
        if (servers.contains(server)) TimoCloud.severe("Tried to add already existing starting server " + server + ". Please report this.");
        servers.add(server);
    }

    public void removeServer(Server server) {
        if (servers.contains(server)) servers.remove(server);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setStartupAmount(int startupAmount) {
        this.startupAmount = startupAmount;
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
        if (ram == 0) TimoCloud.severe("Attention: Group " + name + " has 0MB Ram. (This won't work)");
        this.ram = ram;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setBase(BaseObject base) {
        this.base = base;
    }

    public BaseObject getBase() {
        return base;
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

    public GroupObject toGroupObject() {
        GroupObject groupObject = new GroupObject(
                servers.stream().map(Server::toServerObject).collect(Collectors.toList()),
                getName(),
                getStartupAmount(),
                getMaxAmount(),
                getRam(),
                isStatic(),
                getBaseName(),
                getSortOutStates()
        );
        groupObject.getServers().sort(Comparator.comparing(ServerObject::getName));
        return groupObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group that = (Group) o;
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
