package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.proxy.ProxyOnlinePlayerCountChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxy.ProxyRegisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.proxy.ProxyUnregisterEventBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import cloud.timo.TimoCloud.common.json.JsonConverter;
import cloud.timo.TimoCloud.common.log.LogEntry;
import cloud.timo.TimoCloud.common.log.LogStorage;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.utils.DoAfterAmount;
import cloud.timo.TimoCloud.common.utils.HashUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ProxyObjectCoreImplementation;
import cloud.timo.TimoCloud.core.cloudflare.DnsRecord;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.io.File;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

public class Proxy implements Instance, Communicatable {

    private String name;
    private String id;
    private ProxyGroup group;
    private int port;
    private InetSocketAddress address;
    private Base base;
    private int onlinePlayerCount;
    private final Set<PlayerObject> onlinePlayers;
    private Channel channel;
    private boolean starting;
    private boolean registered;
    private boolean connected;
    private Collection<DnsRecord> dnsRecords;
    private Set<Server> registeredServers;
    private LogStorage logStorage;
    private PublicKey publicKey;

    private DoAfterAmount templateUpdate;

    public Proxy(String name, String id, Base base, ProxyGroup group) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.base = base;
        this.address = new InetSocketAddress(base.getPublicAddress(), 0);
        this.onlinePlayers = Collections.synchronizedSet(new HashSet<>());
        this.registeredServers = new HashSet<>();
        this.logStorage = new LogStorage();
        this.dnsRecords = new HashSet<>();
    }

    @Override
    public void register() {
        if (isRegistered()) return;
        getGroup().onProxyConnect(this);
        this.starting = false;
        this.registered = true;
        for (Server server : getGroup().getRegisteredServers()) registerServer(server);
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ProxyRegisterEventBasicImplementation(toProxyObject()));
    }

    @Override
    public void unregister() {
        if (!isRegistered()) return;
        this.registered = false;
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ProxyUnregisterEventBasicImplementation(toProxyObject()));
    }

    private void onShutdown() {
        getGroup().removeProxy(this);
        getBase().removeProxy(this);
        TimoCloudCore.getInstance().getCloudFlareManager().unregisterProxy(this);
        getBase().sendMessage(Message.create().setType(MessageType.BASE_PROXY_STOPPED).setData(getId()));
    }

    @Override
    public void start() {
        try {
            starting = true;
            Message message = Message.create()
                    .setType(MessageType.BASE_START_PROXY)
                    .set("name", getName())
                    .set("id", getId())
                    .set("group", getGroup().getName())
                    .set("ram", getGroup().getRam())
                    .set("static", getGroup().isStatic())
                    .set("motd", getGroup().getMotd())
                    .set("maxplayers", getGroup().getMaxPlayerCount())
                    .set("maxplayersperproxy", getGroup().getMaxPlayerCountPerProxy())
                    .set("globalHash", HashUtil.getHashes(TimoCloudCore.getInstance().getFileManager().getProxyGlobalDirectory()))
                    .set("javaParameters", getGroup().getJavaParameters())
                    .set("jrePath", getGroup().getJrePath());
            if (!getGroup().isStatic()) {
                File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getProxyTemplatesDirectory(), getGroup().getName());
                try {
                    templateDirectory.mkdirs();
                    message.set("templateHash", HashUtil.getHashes(templateDirectory));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while hashing files while starting proxy " + getName() + ": ");
                    e.printStackTrace();
                    return;
                }
            }
            getBase().sendMessage(message);
            getBase().setReady(false);
            getBase().setAvailableRam(getBase().getAvailableRam() - getGroup().getRam());
            TimoCloudCore.getInstance().info("Told base " + getBase().getName() + " to start proxy " + getName() + ".");
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while starting proxy " + getName() + ": ");
            TimoCloudCore.getInstance().severe(e);
            return;
        }
        getBase().addProxy(this);
        getGroup().addProxy(this);
    }

    @Override
    public void stop() {
        sendMessage(Message.create().setType(MessageType.PROXY_STOP));
        TimoCloudCore.getInstance().getCloudFlareManager().unregisterProxy(this);
    }

    public void registerServer(Server server) {
        sendMessage(Message.create()
                .setType(MessageType.PROXY_ADD_SERVER)
                .set("name", server.getName())
                .set("address", server.getAddress().getAddress().getHostAddress())
                .set("port", server.getPort()));
        if (!registeredServers.contains(server)) registeredServers.add(server);
    }

    public void unregisterServer(Server server) {
        sendMessage(Message.create()
                .setType(MessageType.PROXY_REMOVE_SERVER)
                .set("name", server.getName()));
        registeredServers.remove(server);
    }

    public void onPlayerConnect(PlayerObject playerObject) {
        getOnlinePlayers().add(playerObject);
    }

    public void onPlayerDisconnect(PlayerObject playerObject) {
        getOnlinePlayers().remove(playerObject);
    }

    public void update(PlayerObject playerObject) {
        onPlayerDisconnect(playerObject);
        onPlayerConnect(playerObject);
    }

    @Override
    public void onMessage(Message message, Communicatable sender) {
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case PROXY_STOP:
                stop();
                break;
            case BASE_PROXY_STARTED:
                setPort(((Number) message.get("port")).intValue());
                try {
                    setPublicKey(RSAKeyUtil.publicKeyFromBase64((String) message.get("publicKey")));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe(String.format("Error while setting public key of proxy %s, please report this!", getName()));
                    TimoCloudCore.getInstance().severe(e);
                }
                break;
            case BASE_PROXY_NOT_STARTED:
                //unregister();
                break;
            case PROXY_EXECUTE_COMMAND:
                executeCommand((String) data);
                break;
            case PROXY_SET_PLAYER_COUNT:
                setOnlinePlayerCount(((Number) data).intValue());
                break;
            case PROXY_TRANSFER_FINISHED:
                getTemplateUpdate().addOne();
                break;
            case PROXY_LOG_ENTRY:
                if (isRegistered() && sender instanceof Base) break;
                LogEntry logEntry = JsonConverter.convertMapIfNecessary(data, LogEntry.class);
                logStorage.addEntry(logEntry);
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
    public void onConnect(Channel channel) {
        this.connected = true;
        setChannel(channel);
        register();
        TimoCloudCore.getInstance().info("Proxy " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        this.connected = false;
        setChannel(null);
        unregister();
        TimoCloudCore.getInstance().info("Proxy " + getName() + " disconnected.");
        onShutdown();
    }

    @Override
    public void onHandshakeSuccess() {
        sendMessage(Message.create()
                .setType(MessageType.PROXY_HANDSHAKE_SUCCESS));
    }

    public void executeCommand(String command) {
        sendMessage(Message.create()
                .setType(MessageType.PROXY_EXECUTE_COMMAND)
                .setData(command));
    }

    public void sendPlayer(String playerUUID, Server serverObject) {
        sendMessage(Message.create()
                .setType(MessageType.PROXY_SEND_PLAYER)
                .setData(Message.create()
                        .set("playerUUID", playerUUID)
                        .set("serverObject", serverObject.getName())
                ));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ProxyGroup getGroup() {
        return group;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        this.address = new InetSocketAddress(getAddress().getAddress(), port);
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Base getBase() {
        return base;
    }

    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    public Set<PlayerObject> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isStarting() {
        return starting;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public Collection<DnsRecord> getDnsRecords() {
        return dnsRecords;
    }

    public void addDnsRecord(DnsRecord dnsRecord) {
        this.dnsRecords.add(dnsRecord);
    }

    public Set<Server> getRegisteredServers() {
        return registeredServers;
    }

    public LogStorage getLogStorage() {
        return logStorage;
    }

    public void setOnlinePlayerCount(int onlinePlayerCount) {
        int oldValue = getOnlinePlayerCount();
        this.onlinePlayerCount = onlinePlayerCount;
        if (onlinePlayerCount != oldValue) {
            EventTransmitter.sendEvent(new ProxyOnlinePlayerCountChangeEventBasicImplementation(toProxyObject(), oldValue, onlinePlayerCount));
        }
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        TimoCloudCore.getInstance().getInstanceManager().proxyDataUpdated(this);
    }

    public DoAfterAmount getTemplateUpdate() {
        return templateUpdate;
    }

    public void setTemplateUpdate(DoAfterAmount templateUpdate) {
        this.templateUpdate = templateUpdate;
    }

    public ProxyObject toProxyObject() {
        return new ProxyObjectCoreImplementation(
                getName(),
                getId(),
                getGroup().toLink(),
                getOnlinePlayers().stream().map(player -> ((PlayerObjectBasicImplementation) player).toLink()).collect(Collectors.toSet()),
                getOnlinePlayerCount(),
                getBase().toLink(),
                getAddress()
        );
    }

    public ProxyObjectLink toLink() {
        return new ProxyObjectLink(getId(), getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        return id.equals(proxy.id);
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
