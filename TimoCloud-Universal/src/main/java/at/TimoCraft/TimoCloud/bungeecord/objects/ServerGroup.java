package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 27.12.16.
 */
public class ServerGroup {
    private List<TemporaryServer> temporaryServers = new ArrayList<>();
    private List<TemporaryServer> startingServers = new ArrayList<>();
    private String name;
    private int startupAmount;
    private int ram;
    private boolean isStatic = false;
    private String baseName;
    private BaseObject base;

    public ServerGroup(String name, int startupAmount, int ram, boolean isStatic, String baseName) {
        this.name = name;
        this.startupAmount = startupAmount;
        this.ram = ram;
        this.isStatic = false;
        this.baseName = baseName;
        setBase(TimoCloud.getInstance().getServerManager().getBase(baseName));
    }

    public List<TemporaryServer> getTemporaryServers() {
        return temporaryServers;
    }

    public String getName() {
        return name;
    }

    public int getStartupAmount() {
        return startupAmount;
    }

    public void stopAllServers() {
        List<TemporaryServer> starting = (ArrayList<TemporaryServer>) ((ArrayList) getStartingServers()).clone();
        for (TemporaryServer server : starting) {
            server.stop();
        }
        List<TemporaryServer> temporary = (ArrayList<TemporaryServer>) ((ArrayList) getTemporaryServers()).clone();
        for (TemporaryServer server : temporary) {
            server.stop();
        }
        startingServers = new ArrayList<>();
        temporaryServers = new ArrayList<>();
    }

    public void addServer(TemporaryServer server) {
        if (!temporaryServers.contains(server)) {
            temporaryServers.add(server);
            return;
        }
        TimoCloud.severe("Tried to add already existing server " + server);
    }

    public void addStartingServer(TemporaryServer server) {
        if (!startingServers.contains(server)) {
            startingServers.add(server);
            return;
        }
        TimoCloud.severe("Tried to add already existing starting server " + server);
    }

    public void removeServer(TemporaryServer server) {
        if (temporaryServers.contains(server)) {
            temporaryServers.remove(server);
            return;
        }
        //TimoCloud.severe("Tried to remove not existing server " + server);
    }

    public List<TemporaryServer> getStartingServers() {
        return startingServers;
    }

    public void setStartupAmount(int startupAmount) {
        this.startupAmount = startupAmount;
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
