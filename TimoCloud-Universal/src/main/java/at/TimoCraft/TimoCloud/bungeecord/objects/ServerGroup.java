package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 27.12.16.
 */
public class ServerGroup {
    private List<Server> runningServers = new ArrayList<>();
    private List<Server> startingServers = new ArrayList<>();
    private String name;
    private int startupAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private String baseName;
    private BaseObject base;

    public ServerGroup(String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String baseName) {
        this.name = name;
        this.startupAmount = startupAmount;
        this.maxAmount = maxAmount;
        this.ram = ram;
        if (ram == 0) {
            TimoCloud.severe("Attention: Group " + name + " has 0MB Ram. (This won't work)");
        }
        this.isStatic = isStatic;
        this.baseName = baseName;
        setBase(TimoCloud.getInstance().getServerManager().getBase(baseName));
    }

    public List<Server> getRunningServers() {
        return runningServers;
    }

    public String getName() {
        return name;
    }

    public int getStartupAmount() {
        return startupAmount;
    }

    public void stopAllServers() {
        List<Server> starting = (ArrayList<Server>) ((ArrayList) getStartingServers()).clone();
        for (Server server : starting) {
            server.stop();
        }
        List<Server> temporary = (ArrayList<Server>) ((ArrayList) getRunningServers()).clone();
        for (Server server : temporary) {
            server.stop();
        }
        startingServers = new ArrayList<>();
        runningServers = new ArrayList<>();
    }

    public void onServerConnect(Server server) {
        if (startingServers.contains(server)) startingServers.remove(server);
        if (!runningServers.contains(server)) {
            runningServers.add(server);
            return;
        }
        TimoCloud.severe("Tried to add already existing server " + server);
    }

    public void addStartingServer(Server server) {
        if (!startingServers.contains(server)) {
            startingServers.add(server);
            return;
        }
        TimoCloud.severe("Tried to add already existing starting server " + server);
    }

    public void removeServer(Server server) {
        if (runningServers.contains(server)) {
            runningServers.remove(server);
            return;
        }
        //TimoCloud.severe("Tried to remove not existing server " + server);
    }

    public List<Server> getStartingServers() {
        return startingServers;
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
        this.ram = ram;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
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
