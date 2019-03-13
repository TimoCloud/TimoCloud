package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.internal.links.LinkableObject;
import cloud.timo.TimoCloud.api.internal.links.ServerGroupObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

@NoArgsConstructor
public class ServerGroupObjectBasicImplementation implements ServerGroupObject, LinkableObject<ServerGroupObject> {

    private String id;
    private String name;
    private int startupAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private BaseObjectLink base;
    private Set<String> sortOutStates;
    private Set<ServerObjectLink> servers;

    /**
     * Do not use this - this will be done by TimoCloud
     */
    public ServerGroupObjectBasicImplementation(String id, String name, Set<ServerObjectLink> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, BaseObjectLink base, Set<String> sortOutStates) {
        this.id = id;
        this.name = name;
        this.servers = servers;
        this.startupAmount = startupAmount;
        this.maxAmount = maxAmount;
        this.ram = ram;
        this.isStatic = isStatic;
        this.base = base;
        this.sortOutStates = sortOutStates;
    }

    @Override
    public Collection<ServerObject> getServers() {
        return servers.stream().map(ServerObjectLink::resolve).collect(Collectors.toSet());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOnlineAmount() {
        return startupAmount;
    }

    @Override
    public APIRequestFuture<Void> setOnlineAmount(int value) {
        return new APIRequestImplementation<Void>(SG_SET_ONLINE_AMOUNT, getId(), value).submit();
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public APIRequestFuture<Void> setMaxAmount(int value) {
        return new APIRequestImplementation<Void>(SG_SET_MAX_AMOUNT, getId(), value).submit();
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public APIRequestFuture<Void> setRam(int value) {
        return new APIRequestImplementation<Void>(SG_SET_RAM, getId(), value).submit();
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public APIRequestFuture<Void> setStatic(boolean value) {
        return new APIRequestImplementation<Void>(SG_SET_STATIC, getId(), value).submit();
    }

    @Override
    public BaseObject getBase() {
        return base.resolve();
    }

    @Override
    public APIRequestFuture<Void> setBase(BaseObject value) {
        return new APIRequestImplementation<Void>(SG_SET_BASE, getId(), value).submit();
    }

    @Override
    public Collection<String> getSortOutStates() {
        return sortOutStates;
    }

    @Override
    public APIRequestFuture<Void> setSortOutStates(Collection<String> value) {
        return new APIRequestImplementation<Void>(SG_SET_SORT_OUT_STATES, getId(), value).submit();
    }

    @Override
    public APIRequestFuture<Void> delete() {
        return new APIRequestImplementation<Void>(SG_DELETE, getId()).submit();
    }

    @Override
    public ServerGroupObjectLink toLink() {
        return new ServerGroupObjectLink(this);
    }


}
