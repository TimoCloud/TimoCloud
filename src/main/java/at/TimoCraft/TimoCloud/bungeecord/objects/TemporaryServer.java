package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.utils.TimeUtil;
import io.netty.channel.Channel;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * Created by Timo on 27.12.16.
 */
public class TemporaryServer {
    private ServerInfo serverInfo;
    private boolean registered = false;
    private String name;
    private int port;
    private ServerGroup serverGroup;
    private Channel channel;
    private String state = "STARTING";
    private String extra = "";
    private String players = "0/0";
    private boolean once = false;

    public TemporaryServer(String name, ServerGroup serverGroup, int port, boolean once) {
        this.port = port;
        InetSocketAddress address = InetSocketAddress.createUnresolved("127.0.0.1", getPort());
        serverInfo = TimoCloud.getInstance().getProxy().constructServerInfo(name, address, name, false);
        this.name = name;
        this.serverGroup = serverGroup;
        this.once = once;
    }

    public void start() {

        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash", "-c",
                "cd " + new File(TimoCloud.getInstance().getFileManager().getTemporaryDirectory(), getName()).getAbsolutePath() + " &&" +
                " screen -mdS " + getName() +
                " java -server " +
                " -Xmx" + getServerGroup().getRam() + "M" +
                " -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:MaxGCPauseMillis=50 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 " +
                " -Dcom.mojang.eula.agree=true" +
                " -jar spigot.jar -o false -p " +
                getPort())
                .directory(new File(TimoCloud.getInstance().getFileManager().getTemporaryDirectory(), getName()));
        /*
        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash",
                "-c",
                "./startserver.sh"
                        + " ../../../" + TimoCloud.getInstance().getFileManager().getTemporaryDirectory() + getName()
                        + " " + getName()
                        + " " + getPort()
                        + " " + getServerGroup().getRam())
                .directory(new File(TimoCloud.getInstance().getFileManager().getScriptsDirectory()));
*/
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                TimoCloud.severe("Got response when starting server: " + line);
            }
            getServerGroup().startedPlusOne();
        } catch (Exception e) {
            TimoCloud.severe("Error while starting server " + getName() + ":");
            e.printStackTrace();
        }
    }

    public void stop() {
        TimoCloud.info("Stopping server " + getName() + "...");
        if (channel == null) {
            TimoCloud.info("Did not stop server " + getName() + " because it was not connected. Will stop itself.");
            return;
        }
        channel.close();
        TimoCloud.info("Stopped " + getName() + ".");
    }

    public void register() {
        TimoCloud.getInstance().getProxy().getServers().put(getServerInfo().getName(), getServerInfo());
        serverGroup.getStartingServers().remove(this);
        serverGroup.getTemporaryServers().add(this);
        TimoCloud.getInstance().getServerManager().addServer(getName());
        registered = true;
        TimoCloud.info("Server " + getName() + " connected.");
        setState("ONLINE");
    }

    public void unregister(boolean startNew) {
        if (isRegistered()) {
            getServerGroup().startedMinusOne();
            TimoCloud.getInstance().getServerManager().removeServer(name);
            getServerGroup().removeServer(this);
            setState("OFFLINE");
            registered = false;
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            TimoCloud.info("Server " + getName() + " disconnected.");
            if (TimoCloud.getInstance().isShuttingDown()) {
                return;
            }
            File log = new File(TimoCloud.getInstance().getFileManager().getTemporaryDirectory() + getName() + "/logs/latest.log");
            if (log.exists()) {
                try {
                    File dir = new File(TimoCloud.getInstance().getFileManager().getLogsDirectory() + getName());
                    dir.mkdirs();
                    Files.copy(log.toPath(), new File(dir, TimeUtil.formatTime() + "_" + getName()).toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                TimoCloud.severe("No log from server " + getName() + " exists.");
            }
            if (startNew && !once) {
                TimoCloud.getInstance().getProxy().getScheduler().schedule(TimoCloud.getInstance(), () -> {
                    TimoCloud.getInstance().getServerManager().startServer(serverGroup, name, once);
                    TimoCloud.getInstance().getServerManager().unregisterPort(getPort());
                }, 1, 0, TimeUnit.SECONDS);
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

        if (port != that.port) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }

    public ServerGroup getServerGroup() {
        return serverGroup;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getPort() {
        return port;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public boolean isOnce() {
        return once;
    }
}
