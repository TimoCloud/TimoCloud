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
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.log.LogFractionObject;
import cloud.timo.TimoCloud.common.datatypes.TypeMap;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

@JsonIgnoreProperties({"messageClientAddress"})
@NoArgsConstructor
public class ProxyObjectBasicImplementation implements ProxyObject, LinkableObject<ProxyObject>, Comparable {

    private String name;
    private String id;
    private ProxyGroupObjectLink group;
    private Set<PlayerObjectLink> onlinePlayers;
    private int onlinePlayerCount;
    private BaseObjectLink base;
    private InetSocketAddress inetSocketAddress;
    private MessageClientAddress messageClientAddress;

    public ProxyObjectBasicImplementation(String name, String id, ProxyGroupObjectLink group, Set<PlayerObjectLink> onlinePlayers, int onlinePlayerCount, BaseObjectLink base, InetSocketAddress inetSocketAddress) {
        this.name = name;
        this.id = id;
        this.group = group;
        this.onlinePlayers = onlinePlayers;
        this.onlinePlayerCount = onlinePlayerCount;
        this.base = base;
        this.inetSocketAddress = inetSocketAddress;
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
    public ProxyGroupObject getGroup() {
        return group.resolve();
    }

    @Override
    public List<PlayerObject> getOnlinePlayers() {
        return Collections.unmodifiableList(onlinePlayers.stream().map(PlayerObjectLink::resolve).collect(Collectors.toList()));
    }

    @Override
    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    @Override
    public BaseObject getBase() {
        return base.resolve();
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return inetSocketAddress;
    }

    @Override
    public InetAddress getIpAddress() {
        return inetSocketAddress.getAddress();
    }

    @Override
    public int getPort() {
        return inetSocketAddress.getPort();
    }

    @Override
    public MessageClientAddress getMessageAddress() {
        if (messageClientAddress == null)
            messageClientAddress = new MessageClientAddress(getId(), MessageClientAddressType.PROXY);
        return messageClientAddress;
    }

    @Override
    public APIRequestFuture<Void> executeCommand(String command) {
        return new APIRequestImplementation<Void>(P_EXECUTE_COMMAND, getId(), command).submit();

    }

    @Override
    public APIRequestFuture<Void> stop() {
        return new APIRequestImplementation<Void>(P_STOP, getId()).submit();
    }

    @Override
    public void sendPluginMessage(PluginMessage message) {
        TimoCloudAPI.getMessageAPI().sendMessage(new AddressedPluginMessage(getMessageAddress(), message));
    }

    @Override
    public APIRequestFuture<LogFractionObject> getLogFraction(long startTime, long endTime) {
        return new APIRequestImplementation<LogFractionObject>(
                P_GET_LOG_FRACTION,
                getId(),
                new TypeMap()
                        .put("startTime", startTime)
                        .put("endTime", endTime))
                .submit();
    }

    @Override
    public APIRequestFuture<LogFractionObject> getLogFraction(long startTime) {
        return new APIRequestImplementation<LogFractionObject>(
                P_GET_LOG_FRACTION,
                getId(),
                new TypeMap()
                        .put("startTime", startTime))
                .submit();
    }

    public void addPlayer(PlayerObjectLink playerObjectLink) {
        this.onlinePlayers.add(playerObjectLink);
    }

    public void removePlayer(PlayerObjectLink playerObjectLink) {
        this.onlinePlayers.remove(playerObjectLink);
    }

    @Override
    public ProxyObjectLink toLink() {
        return new ProxyObjectLink(this);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ProxyObject)) return 1;
        ProxyObject so = (ProxyObject) o;
        try {
            return Integer.parseInt(getName().split("-")[getName().split("-").length - 1]) - Integer.parseInt(so.getName().split("-")[so.getName().split("-").length - 1]);
        } catch (Exception e) {
            return getName().compareTo(so.getName());
        }
    }
}
