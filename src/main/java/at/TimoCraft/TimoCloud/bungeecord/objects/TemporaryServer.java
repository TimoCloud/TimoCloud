package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.utils.TimeUtil;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * Created by Timo on 27.12.16.
 */
public class TemporaryServer {
    private ServerInfo serverInfo;
    private boolean registered = false;
    private String name;
    private ServerGroup serverGroup;
    private Process process;
    private ServerClientSocket socket;

    public TemporaryServer(String name, ServerGroup serverGroup) {
        InetSocketAddress address = InetSocketAddress.createUnresolved("127.0.0.1", TimoCloud.getInstance().getServerManager().getFreePort());
        serverInfo = TimoCloud.getInstance().getProxy().constructServerInfo(name, address, name, false);
        this.name = name;
        this.serverGroup = serverGroup;
    }

    public void start() {
        File log = new File(TimoCloud.getInstance().getFileManager().getLogsDirectory(), TimeUtil.formatTime() + "_" + name + ".txt");
        try {
            log.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash",
                "-c",
                "./startserver.sh"
                        + " ../../../" + TimoCloud.getInstance().getFileManager().getTemporaryDirectory() + name
                        + " " + name
                        + " " + TimoCloud.getInstance().getServerManager().getFreePort()
                        + " " + serverGroup.getRam())
                .directory(new File(TimoCloud.getInstance().getFileManager().getScriptsDirectory()))
                .redirectOutput(log).redirectError(log);
        try {
            process = pb.start();

        } catch (Exception e) {
            TimoCloud.severe("Error while starting server " + name + ":");
            e.printStackTrace();
        }
    }

    public void stop() {
        TimoCloud.info("Stopping server " + name + "...");
        process.destroy();
        TimoCloud.info("Stopped " + name + ".");
    }

    public void register() {
        TimoCloud.getInstance().getProxy().getServers().put(serverInfo.getName(), serverInfo);
        serverGroup.getStartingServers().remove(this);
        serverGroup.getTemporaryServers().add(this);
        TimoCloud.getInstance().getServerManager().addServer(name);
        registered = true;
        TimoCloud.info("Server " + name + " connected.");
    }

    public void unregister(boolean startNew) {
        if (registered) {
            TimoCloud.getInstance().getServerManager().removeServer(name);
            TimoCloud.getInstance().getProxy().getServers().remove(serverInfo.getName());
            registered = false;
            TimoCloud.info("Server " + name + " disconnected.");
            if (startNew) {
                TimoCloud.getInstance().getServerManager().startServer(serverGroup, name);
            }
            return;
        }
        TimoCloud.severe("Wanted to unregister not-registered server: " + serverInfo.getName());
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public String getName() {
        return name;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemporaryServer that = (TemporaryServer) o;

        if (registered != that.registered) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = (registered ? 1 : 0);
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }

    public ServerGroup getServerGroup() {
        return serverGroup;
    }

    public Process getProcess() {
        return process;
    }

    public void sendSocketMessage(String message) {
        socket.sendMessage(message);
    }

    public void onSocketMessage(String message) {

    }
}
