package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.cord.CordConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.cord.CordDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.CordObjectLink;
import cloud.timo.TimoCloud.api.objects.CordObject;
import cloud.timo.TimoCloud.api.objects.properties.BaseProperties;
import cloud.timo.TimoCloud.api.objects.properties.CordProperties;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.CordObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cord implements Communicatable, PublicKeyIdentifiable {

    private String id;
    private String name;
    private InetAddress address;
    private int port;
    private Channel channel;

    private PublicKey publicKey;
    private boolean connected;

    public Cord(CordProperties properties) {
        this.id = properties.getId();
        this.name = properties.getName();
        this.publicKey = properties.getPublicKey();
    }


    public Cord(Map<String, Object> properties) throws Exception {
        construct(properties);
    }

    public void construct(CordProperties baseProperties) {
        construct(baseProperties.getId(), baseProperties.getName(), baseProperties.getPublicKey());
    }

    public void construct(String id, String name, PublicKey publicKey) {
        this.id = id;
        this.name = name;
        this.publicKey = publicKey;
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
        properties.put("publicKey", Base64.getEncoder().encodeToString(getPublicKey().getEncoded()));
        return properties;
    }

    @Override
    public void onConnect(Channel channel) {
        this.channel = channel;
        this.address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
        this.connected = true;
        TimoCloudCore.getInstance().info("TimoCloudCord " + getName() + " connected.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new CordConnectEventBasicImplementation(toCordObject()));
    }

    @Override
    public void onDisconnect() {
        this.channel = null;
        this.connected = false;
        TimoCloudCore.getInstance().info("TimoCloudCord " + getName() + " disconnected.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new CordDisconnectEventBasicImplementation(toCordObject()));
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void onMessage(Message message, Communicatable sender) {
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            default:
                sendMessage(message);
                break;
        }
    }

    @Override
    public void sendMessage(Message message) {
        if (getChannel() != null) getChannel().writeAndFlush(message.toJson());
    }

    @Override
    public void onHandshakeSuccess() {
        sendMessage(Message.create().setType(MessageType.CORD_HANDSHAKE_SUCCESS));
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(getAddress(), getPort());
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public CordObject toCordObject() {
        return new CordObjectCoreImplementation(getId(), getName(), getSocketAddress(), isConnected());
    }

    public CordObjectLink toLink() {
        return new CordObjectLink(getId(), getName());
    }
}
