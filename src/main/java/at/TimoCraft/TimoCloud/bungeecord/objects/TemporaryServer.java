package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.utils.TimeUtil;
import io.netty.channel.Channel;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.net.InetSocketAddress;
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

    public TemporaryServer(String name, ServerGroup serverGroup) {
        this.port = TimoCloud.getInstance().getServerManager().getFreePort();
        InetSocketAddress address = InetSocketAddress.createUnresolved("127.0.0.1", getPort());
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
                        + " " + getName()
                        + " " + getPort()
                        + " " + getServerGroup().getRam())
                .directory(new File(TimoCloud.getInstance().getFileManager().getScriptsDirectory()))
                .redirectOutput(log).redirectError(log);
        try {
            pb.start();
        } catch (Exception e) {
            TimoCloud.severe("Error while starting server " + name + ":");
            e.printStackTrace();
        }
    }

    public void stop() {
        TimoCloud.info("Stopping server " + name + "...");
        channel.close();
        TimoCloud.info("Stopped " + name + ".");
    }

    public void register() {
        TimoCloud.getInstance().getProxy().getServers().put(serverInfo.getName(), serverInfo);
        serverGroup.getStartingServers().remove(this);
        serverGroup.getTemporaryServers().add(this);
        TimoCloud.getInstance().getServerManager().addServer(name);
        registered = true;
        TimoCloud.info("Server " + name + " connected.");
        setState("ONLINE");
    }

    public void unregister(boolean startNew) {
        if (registered) {
            TimoCloud.getInstance().getServerManager().removeServer(name);
            registered = false;
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            TimoCloud.info("Server " + name + " disconnected.");
            if (startNew) {
                TimoCloud.getInstance().getProxy().getScheduler().schedule(TimoCloud.getInstance(), () -> {
                    TimoCloud.getInstance().getServerManager().startServer(serverGroup, name);
                    TimoCloud.getInstance().getServerManager().unregisterPort(getPort());
                }, 5, 0, TimeUnit.SECONDS);
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
}
