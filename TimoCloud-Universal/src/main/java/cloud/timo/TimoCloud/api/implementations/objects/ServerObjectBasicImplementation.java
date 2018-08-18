package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddressType;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

@JsonIgnoreProperties({"messageClientAddress"})
public class ServerObjectBasicImplementation implements ServerObject, Comparable {

    private String name;
    private String id;
    private String group;
    protected String state;
    protected String extra;
    private String map;
    private String motd;
    private List<PlayerObject> onlinePlayers;
    private int onlinePlayerCount;
    private int maxPlayerCount;
    private String base;
    private InetSocketAddress socketAddress;
    private MessageClientAddress messageClientAddress;

    public ServerObjectBasicImplementation() {
    }

    public ServerObjectBasicImplementation(String name, String id, String group, String state, String extra, String map, String motd, List<PlayerObject> onlinePlayers, int onlinePlayerCount, int maxPlayerCount, String base, InetSocketAddress socketAddress) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.state = state;
        this.extra = extra;
        this.map = map;
        this.motd = motd;
        this.onlinePlayers = onlinePlayers;
        this.onlinePlayerCount = onlinePlayerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.base = base;
        this.socketAddress = socketAddress;
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
    public ServerGroupObject getGroup() {
        return TimoCloudAPI.getUniversalAPI().getServerGroup(group);
    }

    public String getGroupName() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public APIRequestFuture setState(String state) {
        this.state = state;
        return new APIRequestImplementation(S_SET_STATE, getName(), state).submit();
    }

    @Override
    public String getExtra() {
        return extra;
    }

    @Override
    public APIRequestFuture setExtra(String extra) {
        this.extra = extra;
        return new APIRequestImplementation(S_SET_EXTRA, getName(), extra).submit();
    }

    @Override
    public String getMap() {
        return map;
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public List<PlayerObject> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    @Override
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    @Override
    public BaseObject getBase() {
        return TimoCloudAPI.getUniversalAPI().getBase(base);
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    public InetAddress getIpAddress() {
        return getSocketAddress().getAddress();
    }

    @Override
    public int getPort() {
        return getSocketAddress().getPort();
    }

    @Override
    public boolean isSortedOut() {
        return getGroup().getSortOutStates().contains(getState());
    }

    @Override
    public MessageClientAddress getMessageAddress() {
        if (messageClientAddress == null)
            messageClientAddress = new MessageClientAddress(getId(), MessageClientAddressType.SERVER);
        return messageClientAddress;
    }

    @Override
    public APIRequestFuture executeCommand(String command) {
        return new APIRequestImplementation(S_EXECUTE_COMMAND, getName(), command).submit();

    }

    @Override
    public APIRequestFuture stop() {
        return new APIRequestImplementation(S_STOP, getName()).submit();
    }

    @Override
    public void sendPluginMessage(PluginMessage message) {
        TimoCloudAPI.getMessageAPI().sendMessage(new AddressedPluginMessage(getMessageAddress(), message));
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ServerObject)) return 1;
        ServerObject so = (ServerObject) o;
        try {
            return Integer.parseInt(getName().split("-")[getName().split("-").length - 1]) - Integer.parseInt(so.getName().split("-")[so.getName().split("-").length - 1]);
        } catch (Exception e) {
            return getName().compareTo(so.getName());
        }
    }
}
