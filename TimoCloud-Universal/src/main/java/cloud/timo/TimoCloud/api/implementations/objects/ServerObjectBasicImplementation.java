package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.internal.links.*;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddressType;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.api.objects.log.LogFractionObject;
import cloud.timo.TimoCloud.common.datatypes.TypeMap;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

@JsonIgnoreProperties({"messageClientAddress"})
@NoArgsConstructor
public class ServerObjectBasicImplementation implements ServerObject, LinkableObject<ServerObject>, Comparable {

    private String name;
    private String id;
    private ServerGroupObjectLink group;
    protected String state;
    protected String extra;
    private String map;
    private String motd;
    private Collection<PlayerObjectLink> onlinePlayers;
    private int onlinePlayerCount;
    private int maxPlayerCount;
    private BaseObjectLink base;
    private InetSocketAddress socketAddress;
    private MessageClientAddress messageClientAddress;

    public ServerObjectBasicImplementation(String name, String id, ServerGroupObjectLink group, String state, String extra, String map, String motd, Set<PlayerObjectLink> onlinePlayers, int onlinePlayerCount, int maxPlayerCount, BaseObjectLink base, InetSocketAddress socketAddress) {
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
        return group.resolve();
    }

    public void setGroup(ServerGroupObject group) {
        this.group = ((ServerGroupObjectBasicImplementation) group).toLink();
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public APIRequestFuture<Void> setState(String state) {
        this.state = state;
        return new APIRequestImplementation<Void>(S_SET_STATE, getId(), state).submit();
    }

    @Override
    public String getExtra() {
        return extra;
    }

    @Override
    public APIRequestFuture<Void> setExtra(String extra) {
        this.extra = extra;
        return new APIRequestImplementation<Void>(S_SET_EXTRA, getId(), extra).submit();
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
        return onlinePlayers.stream().map(PlayerObjectLink::resolve).collect(Collectors.toList());
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
        return base.resolve();
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
    public APIRequestFuture<Void> executeCommand(String command) {
        return new APIRequestImplementation<Void>(S_EXECUTE_COMMAND, getId(), command).submit();
    }

    @Override
    public APIRequestFuture<Void> stop() {
        return new APIRequestImplementation<Void>(S_STOP, getId()).submit();
    }

    @Override
    public void sendPluginMessage(PluginMessage message) {
        TimoCloudAPI.getMessageAPI().sendMessage(new AddressedPluginMessage(getMessageAddress(), message));
    }

    @Override
    public APIRequestFuture<LogFractionObject> getLogFraction(long startTime, long endTime) {
        return new APIRequestImplementation<LogFractionObject>(
                S_GET_LOG_FRACTION,
                getId(),
                new TypeMap()
                        .put("startTime", startTime)
                        .put("endTime", endTime))
                .submit();
    }

    @Override
    public APIRequestFuture<LogFractionObject> getLogFraction(long startTime) {
        return new APIRequestImplementation<LogFractionObject>(
                S_GET_LOG_FRACTION,
                getId(),
                new TypeMap()
                        .put("startTime", startTime))
                .submit();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public ServerObjectLink toLink() {
        return new ServerObjectLink(this);
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

    public void addPlayer(PlayerObjectLink playerObjectLink) {
        this.onlinePlayers.add(playerObjectLink);
    }

    public void removePlayer(PlayerObjectLink playerObjectLink) {
        this.onlinePlayers.remove(playerObjectLink);
    }

    public void setNameInternally(String name){
        this.name = name;
    }

    public void setIdInternally(String id){
        this.id = id;
    }

    public void setServerGroupObjectLink(ServerGroupObjectLink serverGroupObjectLink){
        this.group = serverGroupObjectLink;
    }

    public void setStateInternally(String state){
        this.state = state;
    }

    public void setExtraInternally(String extra){
        this.extra = extra;
    }

    public void setMapInternally(String map){
        this.map = map;
    }

    public void setMotdInternally(String motd){
        this.motd = motd;
    }

    public void setOnlinePlayerCountInternally(int i){
        this.onlinePlayerCount = i;
    }

    public void setMaxPlayerCountInternally(int i){
        this.maxPlayerCount = i;
    }

    public void setBaseObjectLinkInternally(BaseObjectLink base){
        this.base = base;
    }

    public void setSocketAddressInternally(InetSocketAddress inetSocketAddress){
        this.socketAddress = inetSocketAddress;
    }

    public void setMessageClientAddressInternally(MessageClientAddress messageClientAddress){
        this.messageClientAddress = messageClientAddress;
    }

}
