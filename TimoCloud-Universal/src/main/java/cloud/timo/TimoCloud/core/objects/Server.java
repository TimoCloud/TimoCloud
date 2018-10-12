package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.ServerRegisterEvent;
import cloud.timo.TimoCloud.api.events.ServerUnregisterEvent;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ServerObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.lib.messages.Message;
import cloud.timo.TimoCloud.lib.utils.DoAfterAmount;
import cloud.timo.TimoCloud.lib.utils.HashUtil;
import io.netty.channel.Channel;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class Server implements Instance, Communicatable {

    private String name;
    private String id;
    private Integer port;
    private ServerGroup group;
    private Channel channel;
    private Base base;
    private InetSocketAddress address;
    private String state = "STARTING";
    private String extra = "";
    private String motd = "";
    private final Set<PlayerObject> onlinePlayers;
    private int onlinePlayerCount = 0;
    private int maxPlayers = 0;
    private String map;
    private boolean starting;
    private boolean registered = false;

    private DoAfterAmount templateUpdate;

    public Server(String name, String id, Base base, String map, ServerGroup group) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.base = base;
        this.address = new InetSocketAddress(base.getAddress(), 0);
        this.onlinePlayers = Collections.synchronizedSet(new HashSet<>());
        this.map = map;
        if (this.map == null) this.map = "";
    }

    public boolean isStatic() {
        return getGroup().isStatic();
    }

    @Override
    public void start() {
        try {
            this.starting = true;
            Message message = Message.create()
                    .setType("START_SERVER")
                    .set("name", getName())
                    .set("id", getId())
                    .set("group", getGroup().getName())
                    .set("ram", getGroup().getRam())
                    .set("static", getGroup().isStatic())
                    .setIfNotNull("map", getMap())
                    .set("globalHash", HashUtil.getHashes(TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory()));
            if (!getGroup().isStatic()) {
                File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), getGroup().getName());
                File mapDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), getGroup().getName() + "_" + getMap());
                try {
                    templateDirectory.mkdirs();
                    if (hasMap()) mapDirectory.mkdirs();
                    message.set("templateHash", HashUtil.getHashes(templateDirectory));
                    if (hasMap()) message.set("mapHash", HashUtil.getHashes(mapDirectory));
                } catch (IOException e) {
                    TimoCloudCore.getInstance().severe("Error while hashing files while starting server " + getName() + ": ");
                    e.printStackTrace();
                    return;
                }
            }
            getBase().sendMessage(message);
            getBase().setReady(false);
            getBase().setAvailableRam(getBase().getAvailableRam() - getGroup().getRam());
            TimoCloudCore.getInstance().info("Told base " + getBase().getName() + " to start server " + getName() + ".");
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while starting server " + getName() + ": ");
            TimoCloudCore.getInstance().severe(e);
            return;
        }
        getGroup().addStartingServer(this);
        getBase().addServer(this);
    }

    @Override
    public void stop() {
        if (getChannel() != null) getChannel().close();
        unregister();
    }

    @Override
    public void onConnect(Channel channel) {
        setChannel(channel);
        TimoCloudCore.getInstance().info("Server " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        setChannel(null);
        TimoCloudCore.getInstance().info("Server " + getName() + " disconnected.");
        unregister();
    }

    @Override
    public void register() {
        if (isRegistered()) return;
        getGroup().onServerConnect(this);
        setState("ONLINE");
        for (ProxyGroup proxyGroup : TimoCloudCore.getInstance().getInstanceManager().getProxyGroups()) {
            if (!proxyGroup.getServerGroups().contains(getGroup())) continue;
            proxyGroup.registerServer(this);
        }
        this.starting = false;
        this.registered = true;
        TimoCloudCore.getInstance().info("Server " + getName() + " registered.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ServerRegisterEvent(toServerObject()));
    }

    @Override
    public void unregister() {
        if (! isRegistered()) return;
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ServerUnregisterEvent(toServerObject()));
        getGroup().removeServer(this);
        getBase().removeServer(this);
        setState("OFFLINE");
        for (ProxyGroup proxyGroup : TimoCloudCore.getInstance().getInstanceManager().getProxyGroups()) {
            if (!proxyGroup.getServerGroups().contains(getGroup())) continue;
            proxyGroup.unregisterServer(this);
        }
        this.registered = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getBase().sendMessage(Message.create().setType("SERVER_STOPPED").setData(getId()));
            }
        }, 300000);
    }

    public void onPlayerConnect(PlayerObject playerObject) {
        if (!getOnlinePlayers().contains(playerObject)) getOnlinePlayers().add(playerObject);
    }

    public void onPlayerDisconnect(PlayerObject playerObject) {
        if (getOnlinePlayers().contains(playerObject)) getOnlinePlayers().remove(playerObject);
    }

    @Override
    public void onMessage(Message message) {
        String type = (String) message.get("type");
        Object data = message.get("data");
        switch (type) {
            case "SET_STATE":
                setState((String) data);
                break;
            case "SET_EXTRA":
                setExtra((String) data);
                break;
            case "SET_MOTD":
                setMotd((String) data);
                break;
            case "SET_MAP":
                setMap((String) data);
                break;
            case "SET_PLAYERS":
                setOnlinePlayerCount(Integer.parseInt(((String) data).split("/")[0]));
                setMaxPlayers(Integer.parseInt(((String) data).split("/")[1]));
                break;
            case "EXECUTE_COMMAND":
                executeCommand((String) data);
                break;
            case "STOP_SERVER":
                stop();
                break;
            case "SERVER_STARTED":
                setPort(((Number) message.get("port")).intValue());
                break;
            case "SERVER_NOT_STARTED":
                //unregister();
                break;
            case "REGISTER":
                register();
                break;
            case "TRANSFER_FINISHED":
                getTemplateUpdate().addOne();
                break;
            default:
                sendMessage(message);
        }
    }

    @Override
    public void sendMessage(Message message) {
        if (getChannel() != null) getChannel().writeAndFlush(message.toJson());
    }

    @Override
    public void onHandshakeSuccess() {
        sendMessage(Message.create().setType("HANDSHAKE_SUCCESS"));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public ServerGroup getGroup() {
        return group;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    public Base getBase() {
        return base;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
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

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public Set<PlayerObject> getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
        setAddress(new InetSocketAddress(getAddress().getAddress(), port));
    }

    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    public void setOnlinePlayerCount(int onlinePlayerCount) {
        this.onlinePlayerCount = onlinePlayerCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public boolean hasMap() {
        return ! (getMap() == null || getMap().isEmpty());
    }

    public boolean isStarting() {
        return starting;
    }


    public DoAfterAmount getTemplateUpdate() {
        return templateUpdate;
    }

    public void setTemplateUpdate(DoAfterAmount templateUpdate) {
        this.templateUpdate = templateUpdate;
    }

    public void executeCommand(String command) {
        sendMessage(Message.create().setType("EXECUTE_COMMAND").setData(command));
    }

    public ServerObject toServerObject() {
        return new ServerObjectCoreImplementation(
                getName(),
                getId(),
                getGroup().getName(),
                getState(),
                getExtra(),
                getMap(),
                getMotd(),
                new ArrayList<>(getOnlinePlayers()),
                getOnlinePlayerCount(),
                getMaxPlayers(),
                getBase() == null ? null : getBase().getName(),
                getAddress()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        return id.equals(server.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
