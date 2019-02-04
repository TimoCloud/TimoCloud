package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.propertyChanges.base.*;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.properties.BaseProperties;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.BaseObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.lib.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.lib.events.EventTransmitter;
import cloud.timo.TimoCloud.lib.protocol.Message;
import cloud.timo.TimoCloud.lib.protocol.MessageType;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

public class Base implements PublicKeyIdentifiable, Communicatable {

    private String id;
    private String name;
    private InetAddress address;
    private InetAddress publicAddress; // Used for connecting to public proxies
    private Channel channel;
    private int availableRam;
    private int maxRam;
    private int keepFreeRam;
    private double cpuLoad;
    private double maxCpuLoad;
    private PublicKey publicKey;
    private boolean connected;
    private boolean ready;
    private Set<Server> servers;
    private Set<Proxy> proxies;

    public Base(BaseProperties properties) {
        construct(properties);
    }

    public Base(String id, String name, int maxRam, int keepFreeRam, double maxCpuLoad, PublicKey publicKey) {
        construct(id, name, maxRam, keepFreeRam, maxCpuLoad, publicKey);
    }

    public Base(Map<String, Object> properties) throws Exception {
        construct(properties);
    }

    public void construct(BaseProperties baseProperties) {
        construct(baseProperties.getId(), baseProperties.getName(), baseProperties.getMaxRam(), baseProperties.getKeepFreeRam(), baseProperties.getMaxCpuLoad(), baseProperties.getPublicKey());
    }

    public void construct(String id, String name, int maxRam, int keepFreeRam, double maxCpuLoad, PublicKey publicKey) {
        this.id = id;
        this.name = name;
        this.maxRam = maxRam;
        this.keepFreeRam = keepFreeRam;
        this.maxCpuLoad = maxCpuLoad;
        this.publicKey = publicKey;
        this.servers = new HashSet<>();
        this.proxies = new HashSet<>();
    }

    public void construct(Map<String, Object> properties) throws Exception {
        try {
            String id = (String) properties.get("id");
            String name = (String) properties.get("name");
            String publicKeyString = (String) properties.get("publicKey");
            PublicKey publicKey;
            try {
                publicKey = RSAKeyUtil.publicKeyFromBase64(publicKeyString);
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe(e);
                throw new IllegalArgumentException(String.format("Invalid RSA public key: %s", publicKeyString));
            }
            BaseProperties defaultProperties = new BaseProperties(id, name, publicKey);
            construct(
                    id,
                    name,
                    ((Number) properties.getOrDefault("maxRam", defaultProperties.getMaxRam())).intValue(),
                    ((Number) properties.getOrDefault("keepFreeRam", defaultProperties.getKeepFreeRam())).intValue(),
                    ((Number) properties.getOrDefault("maxCpuLoad", defaultProperties.getMaxCpuLoad())).doubleValue(),
                    publicKey
            );

        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + properties.get("name") + "':");
            e.printStackTrace();
        }
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", getId());
        properties.put("name", getName());
        properties.put("maxRam", getMaxRam());
        properties.put("keepFreeRam", getKeepFreeRam());
        properties.put("maxCpuLoad", getMaxCpuLoad());
        properties.put("publicKey", Base64.getEncoder().encodeToString(getPublicKey().getEncoded()));
        return properties;
    }

    @Override
    @Deprecated
    public void onConnect(Channel channel) {

    }

    public void onConnect(Channel channel, InetAddress address, InetAddress publicAddress) {
        setChannel(channel);
        setConnected(true);
        setReady(false);
        setAddress(address);
        setPublicAddress(publicAddress);
        TimoCloudCore.getInstance().getCloudFlareManager().onBaseRegisterEvent(this);
        TimoCloudCore.getInstance().info("Base " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        setChannel(null);
        setConnected(false);
        setReady(false);
        setCpuLoad(0);
        setAvailableRam(0);
        TimoCloudCore.getInstance().getCloudFlareManager().onBaseUnregisterEvent(this);
        TimoCloudCore.getInstance().info("Base " + getName() + " disconnected.");
    }

    @Override
    public void onMessage(Message message, Communicatable sender) {
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case BASE_RESOURCES:
                Map map = (Map) data;
                int usedRam = servers.stream().mapToInt((server) -> server.getGroup().getRam()).sum() + proxies.stream().mapToInt((proxy) -> proxy.getGroup().getRam()).sum();
                int availableRam = Math.max(0, ((Number) map.get("freeRam")).intValue() - getKeepFreeRam());
                setAvailableRam(Math.max(0, Math.min(availableRam, maxRam - usedRam)));
                double cpuLoad = (Double) map.get("cpuLoad");
                setCpuLoad(cpuLoad);
                boolean ready = (boolean) map.get("ready") && cpuLoad <= getMaxCpuLoad();
                setReady(ready);
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
        sendMessage(Message.create().setType(MessageType.BASE_HANDSHAKE_SUCCESS));
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = getName();
        this.name = name;
        TimoCloudCore.getInstance().getInstanceManager().baseDataUpdated(this);
        EventTransmitter.sendEvent(new BaseNameChangedEvent(toBaseObject(), oldValue, name));
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        InetAddress oldValue = getAddress();
        this.address = address;
        EventTransmitter.sendEvent(new BaseAddressChangedEvent(toBaseObject(), oldValue, address));
    }

    public InetAddress getPublicAddress() {
        return publicAddress;
    }

    public Base setPublicAddress(InetAddress publicAddress) {
        InetAddress oldValue = getPublicAddress();
        this.publicAddress = publicAddress;
        EventTransmitter.sendEvent(new BasePublicAddressChangedEvent(toBaseObject(), oldValue, publicAddress));
        return this;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    public int getAvailableRam() {
        return availableRam;
    }

    public void setAvailableRam(int availableRam) {
        int oldValue = getAvailableRam();
        this.availableRam = availableRam;
        EventTransmitter.sendEvent(new BaseAvailableRamChangedEvent(toBaseObject(), oldValue, availableRam));
    }

    public int getMaxRam() {
        return maxRam;
    }

    public void setMaxRam(int maxRam) {
        int oldValue = getMaxRam();
        this.maxRam = maxRam;
        EventTransmitter.sendEvent(new BaseMaxRamChangedEvent(toBaseObject(), oldValue, maxRam));
    }

    public int getKeepFreeRam() {
        return keepFreeRam;
    }

    public Base setKeepFreeRam(int keepFreeRam) {
        int oldValue = getKeepFreeRam();
        this.keepFreeRam = keepFreeRam;
        EventTransmitter.sendEvent(new BaseKeepFreeRamChangedEvent(toBaseObject(), oldValue, keepFreeRam));
        return this;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        double oldValue = getCpuLoad();
        this.cpuLoad = cpuLoad;
        EventTransmitter.sendEvent(new BaseCpuLoadChangedEvent(toBaseObject(), oldValue, cpuLoad));
    }

    public double getMaxCpuLoad() {
        return maxCpuLoad;
    }

    public Base setMaxCpuLoad(double maxCpuLoad) {
        double oldValue = getMaxCpuLoad();
        this.maxCpuLoad = maxCpuLoad;
        EventTransmitter.sendEvent(new BaseMaxCpuLoadChangedEvent(toBaseObject(), oldValue, maxCpuLoad));
        return this;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        boolean oldValue = isConnected();
        this.connected = connected;
        EventTransmitter.sendEvent(new BaseConnectedChangedEvent(toBaseObject(), oldValue, connected));
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        boolean oldValue = isReady();
        this.ready = ready;
        EventTransmitter.sendEvent(new BaseReadyChangedEvent(toBaseObject(), oldValue, ready));
    }

    public Set<Server> getServers() {
        return servers;
    }

    public Set<Proxy> getProxies() {
        return proxies;
    }

    public void addServer(Server server) {
        if (getServers().contains(server)) return;
        getServers().add(server);
    }

    public void removeServer(Server server) {
        if (!getServers().contains(server)) return;
        getServers().remove(server);
    }

    public void addProxy(Proxy proxy) {
        if (getProxies().contains(proxy)) return;
        getProxies().add(proxy);
    }

    public void removeProxy(Proxy proxy) {
        if (!getProxies().contains(proxy)) return;
        getProxies().remove(proxy);
    }

    public BaseObject toBaseObject() {
        return new BaseObjectCoreImplementation(
                getId(),
                getName(),
                getPublicAddress(),
                getCpuLoad(),
                getAvailableRam(),
                getMaxRam(),
                isConnected(),
                isReady(),
                getServers().stream().map(Server::toServerObject).collect(Collectors.toSet()),
                getProxies().stream().map(Proxy::toProxyObject).collect(Collectors.toSet())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Base base = (Base) o;

        return name != null ? name.equals(base.name) : base.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
