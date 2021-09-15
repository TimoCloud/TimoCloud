package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.base.BaseAddressChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseAvailableRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseCpuLoadChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseKeepFreeRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseMaxCpuLoadChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseMaxRamChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseNameChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseNotReadyEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BasePublicAddressChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.base.BaseReadyEventBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.properties.BaseProperties;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.BaseObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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

    /**
     * When set to "AUTO", the base's public IP address (which is being passed on to servers and proxies running on this base and thereby used by players to connect) will be determined automatically. Otherwise, the given value will be used.
     */
    private String publicIpConfig;

    public Base(BaseProperties properties) {
        construct(properties);
    }

    public Base(String id, String name, int maxRam, int keepFreeRam, double maxCpuLoad, String publicIpConfig, PublicKey publicKey) {
        construct(id, name, maxRam, keepFreeRam, maxCpuLoad, publicIpConfig, publicKey);
    }

    public Base(Map<String, Object> properties) throws Exception {
        construct(properties);
    }

    public void construct(BaseProperties baseProperties) {
        construct(baseProperties.getId(), baseProperties.getName(), baseProperties.getMaxRam(), baseProperties.getKeepFreeRam(), baseProperties.getMaxCpuLoad(), baseProperties.getPublicIpConfig(), baseProperties.getPublicKey());
    }

    public void construct(String id, String name, int maxRam, int keepFreeRam, double maxCpuLoad, String publicIpConfig, PublicKey publicKey) {
        this.id = id;
        this.name = name;
        this.maxRam = maxRam;
        this.keepFreeRam = keepFreeRam;
        this.maxCpuLoad = maxCpuLoad;
        this.publicIpConfig = publicIpConfig;
        this.publicKey = publicKey;
        this.servers = new HashSet<>();
        this.proxies = new HashSet<>();
    }

    public void construct(Map<String, Object> properties) {
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
                    (String) properties.getOrDefault("publicAddress", defaultProperties.getPublicIpConfig()),
                    publicKey
            );

        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + properties.get("name") + "':");
            TimoCloudCore.getInstance().severe(e);
        }
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", getId());
        properties.put("name", getName());
        properties.put("maxRam", getMaxRam());
        properties.put("keepFreeRam", getKeepFreeRam());
        properties.put("maxCpuLoad", getMaxCpuLoad());
        properties.put("publicAddress", getPublicIpConfig());
        properties.put("publicKey", Base64.getEncoder().encodeToString(getPublicKey().getEncoded()));
        return properties;
    }

    @Override
    @Deprecated
    public void onConnect(Channel channel) {
    }

    public void onConnect(Channel channel, InetAddress address, InetAddress publicAddress) {
        setChannel(channel);
        setPublicAddress(publicAddress);
        setAddress(address);
        setReady(false);
        setConnected(true); // Also fires event
        TimoCloudCore.getInstance().info("Base " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        setChannel(null);
        setConnected(false);
        setReady(false);
        setCpuLoad(0);
        setAvailableRam(0);
        TimoCloudCore.getInstance().info("Base " + getName() + " disconnected.");
    }

    @Override
    public void onMessage(Message message, Communicatable sender) {
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case BASE_RESOURCES:
                Map<?, ?> map = (Map<?, ?>) data;
                int usedRam = servers.stream().mapToInt((server) -> server.getGroup().getRam()).sum() + proxies.stream().mapToInt((proxy) -> proxy.getGroup().getRam()).sum();
                int availableRam = Math.max(0, ((Number) map.get("freeRam")).intValue() - getKeepFreeRam());
                setAvailableRam(Math.max(0, Math.min(availableRam, maxRam - usedRam)));
                double cpuLoad = (Double) map.get("cpuLoad");
                setCpuLoad(cpuLoad);
                boolean ready = (Boolean) map.get("ready") && cpuLoad <= getMaxCpuLoad();
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
        EventTransmitter.sendEvent(new BaseNameChangeEventBasicImplementation(toBaseObject(), oldValue, name));
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        InetAddress oldValue = getAddress();
        this.address = address;
        EventTransmitter.sendEvent(new BaseAddressChangeEventBasicImplementation(toBaseObject(), oldValue, address));
    }

    public String getPublicIpConfig() {
        return this.publicIpConfig;
    }

    public Base setPublicIpConfig(String publicIpConfig) {
        this.publicIpConfig = publicIpConfig;
        return this;
    }

    public InetAddress getPublicAddress() {
        return publicAddress;
    }

    public Base setPublicAddress(InetAddress publicAddress) {
        InetAddress oldValue = getPublicAddress();
        this.publicAddress = publicAddress;
        EventTransmitter.sendEvent(new BasePublicAddressChangeEventBasicImplementation(toBaseObject(), oldValue, publicAddress));
        return this;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getAvailableRam() {
        return availableRam;
    }

    public void setAvailableRam(int availableRam) {
        int oldValue = getAvailableRam();
        this.availableRam = availableRam;
        EventTransmitter.sendEvent(new BaseAvailableRamChangeEventBasicImplementation(toBaseObject(), oldValue, availableRam));
    }

    public int getMaxRam() {
        return maxRam;
    }

    public void setMaxRam(int maxRam) {
        int oldValue = getMaxRam();
        this.maxRam = maxRam;
        EventTransmitter.sendEvent(new BaseMaxRamChangeEventBasicImplementation(toBaseObject(), oldValue, maxRam));
    }

    public int getKeepFreeRam() {
        return keepFreeRam;
    }

    public Base setKeepFreeRam(int keepFreeRam) {
        int oldValue = getKeepFreeRam();
        this.keepFreeRam = keepFreeRam;
        EventTransmitter.sendEvent(new BaseKeepFreeRamChangeEventBasicImplementation(toBaseObject(), oldValue, keepFreeRam));
        return this;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        double oldValue = getCpuLoad();
        this.cpuLoad = cpuLoad;
        EventTransmitter.sendEvent(new BaseCpuLoadChangeEventBasicImplementation(toBaseObject(), oldValue, cpuLoad));
    }

    public double getMaxCpuLoad() {
        return maxCpuLoad;
    }

    public Base setMaxCpuLoad(double maxCpuLoad) {
        double oldValue = getMaxCpuLoad();
        this.maxCpuLoad = maxCpuLoad;
        EventTransmitter.sendEvent(new BaseMaxCpuLoadChangeEventBasicImplementation(toBaseObject(), oldValue, maxCpuLoad));
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
        if (oldValue != connected) {
            if (connected) EventTransmitter.sendEvent(new BaseConnectEventBasicImplementation(toBaseObject()));
            else EventTransmitter.sendEvent(new BaseDisconnectEventBasicImplementation(toBaseObject()));
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        boolean oldValue = isReady();
        this.ready = ready;
        if (oldValue != ready) {
            if (ready) EventTransmitter.sendEvent(new BaseReadyEventBasicImplementation(toBaseObject()));
            else EventTransmitter.sendEvent(new BaseNotReadyEventBasicImplementation(toBaseObject()));
        }
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
                getMaxCpuLoad(),
                getAvailableRam(),
                getMaxRam(),
                isConnected(),
                isReady(),
                getServers().stream().map(Server::toLink).collect(Collectors.toSet()),
                getProxies().stream().map(Proxy::toLink).collect(Collectors.toSet())
        );
    }

    public BaseObjectLink toLink() {
        return new BaseObjectLink(getId(), getName());
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
