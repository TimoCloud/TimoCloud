package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Base implements Communicatable {
    private String name;
    private InetAddress address;
    private InetAddress publicAddress; // Used for connecting to public proxies
    private Channel channel;
    private int availableRam;
    private int maxRam;
    private double cpu;
    private boolean connected;
    private boolean ready;
    private List<Server> servers;
    private List<Proxy> proxies;

    public Base(String name, InetAddress address, InetAddress publicAddress, Channel channel) {
        this.name = name;
        this.address = address;
        this.publicAddress = publicAddress;
        this.channel = channel;
        setReady(false);
        servers = new ArrayList<>();
        proxies = new ArrayList<>();
    }

    @Override
    public void onConnect(Channel channel) {
        setChannel(channel);
        setConnected(true);
        setReady(true);
        TimoCloudCore.getInstance().getCloudFlareManager().onBaseRegisterEvent(this);
        TimoCloudCore.getInstance().info("Base " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        setChannel(null);
        setConnected(false);
        setReady(false);
        TimoCloudCore.getInstance().getCloudFlareManager().onBaseUnregisterEvent(this);
        TimoCloudCore.getInstance().info("Base " + getName() + " disconnected.");
    }

    @Override
    public void onMessage(JSONObject message) {
        String type = (String) message.get("type");
        Object data = message.get("data");
        switch (type) {
            case "RESOURCES":
                JSONObject map = (JSONObject) message.get("data");
                setReady((boolean) map.get("ready"));
                int maxRam = ((Number) map.get("maxRam")).intValue();
                int usedRam = servers.stream().mapToInt((server) -> server.getGroup().getRam()).sum() + proxies.stream().mapToInt((proxy) -> proxy.getGroup().getRam()).sum();
                int availableRam = ((Number) map.get("availableRam")).intValue();
                setAvailableRam(Math.max(0, Math.min(availableRam, maxRam-usedRam)));
                setCpu(((Double) map.get("cpu")));
                setMaxRam(maxRam);
                break;
            default:
                sendMessage(message);
        }
    }

    @Override
    public void sendMessage(JSONObject message) {
        if (getChannel() != null) getChannel().writeAndFlush(message.toString());
    }

    @Override
    public void onHandshakeSuccess() {
        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(getChannel(), "HANDSHAKE_SUCCESS", null);
    }
    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public InetAddress getPublicAddress() {
        return publicAddress;
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
        this.availableRam = availableRam;
    }

    public int getMaxRam() {
        return maxRam;
    }

    public void setMaxRam(int maxRam) {
        this.maxRam = maxRam;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<Proxy> getProxies() {
        return proxies;
    }
}
