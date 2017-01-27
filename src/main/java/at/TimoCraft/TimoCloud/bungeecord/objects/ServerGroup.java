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

    public ServerGroup(String name, int startupAmount, int ram) {
        this.name = name;
        this.startupAmount = startupAmount;
        this.ram = ram;
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

    public void startAllServers() {
        File directory = new File(TimoCloud.getInstance().getFileManager().getTemplatesDirectory() + name);
        File spigot = new File(directory, "spigot.jar");
        if (! spigot.exists()) {
            TimoCloud.severe("Could not start group " + getName() + " because spigot.jar does not exist.");
            return;
        }
        for (int i = 1; i <= getStartupAmount(); i++) {
            String name = getName() + "-" + i;
            if (!TimoCloud.getInstance().getServerManager().isRunning(name)) {
                TimoCloud.getInstance().getServerManager().startServer(this, name);
            }
        }
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

    public int getRam() {
        return ram;
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
