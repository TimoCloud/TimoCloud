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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_DELETE;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_BASE;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_JAVA_START_PARAMETERS;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_MAX_AMOUNT;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_ONLINE_AMOUNT;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_PRIORITY;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_RAM;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_SORT_OUT_STATES;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_SPIGOT_START_PARAMETERS;
import static cloud.timo.TimoCloud.api.async.APIRequestType.SG_SET_STATIC;

@NoArgsConstructor
public class ServerGroupObjectBasicImplementation implements ServerGroupObject, LinkableObject<ServerGroupObject> {

    // Assign short json property names so that the JSON object is smaller
    @JsonProperty("i")
    private String id;
    @JsonProperty("n")
    private String name;
    @JsonProperty("oa")
    private int onlineAmount;
    @JsonProperty("ma")
    private int maxAmount;
    @JsonProperty("r")
    private int ram;
    @JsonProperty("s")
    private boolean isStatic;
    @JsonProperty("p")
    private int priority;
    @JsonProperty("b")
    private BaseObjectLink base;
    @JsonProperty("so")
    private Set<String> sortOutStates;
    @JsonProperty("jp")
    private List<String> javaParameters;
    @JsonProperty("sp")
    private List<String> spigotParameters;
    @JsonProperty("se")
    private Set<ServerObjectLink> servers;

    /**
     * Do not use this - this will be done by TimoCloud
     */
    public ServerGroupObjectBasicImplementation(String id, String name, Set<ServerObjectLink> servers, int onlineAmount, int maxAmount, int ram, boolean isStatic, int priority, BaseObjectLink base, Set<String> sortOutStates, List<String> javaParameters, List<String> spigotParameters) {
        this.id = id;
        this.name = name;
        this.servers = servers;
        this.onlineAmount = onlineAmount;
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
        return onlineAmount;
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
    public int getPriority() {
        return priority;
    }

    @Override
    public APIRequestFuture<Void> setPriority(int value) {
        return new APIRequestImplementation<Void>(SG_SET_PRIORITY, getId(), value).submit();
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
    public Collection<String> getJavaParameters() {
        return javaParameters;
    }

    @Override
    public APIRequestFuture<Void> setJavaParameters(Collection<String> value) {
        return new APIRequestImplementation<Void>(SG_SET_JAVA_START_PARAMETERS, getId(), value).submit();
    }

    @Override
    public Collection<String> getSpigotParameters() {
        return spigotParameters;
    }

    @Override
    public APIRequestFuture<Void> setSpigotParameters(Collection<String> value) {
        return new APIRequestImplementation<Void>(SG_SET_SPIGOT_START_PARAMETERS, getId(), value).submit();
    }

    @Override
    public ServerGroupObjectLink toLink() {
        return new ServerGroupObjectLink(this);
    }

    public void setNameInternally(String name) {
        this.name = name;
    }

    public void setOnlineAmountInternally(int i) {
        this.onlineAmount = i;
    }

    public void setMaxAmoutInternally(int i) {
        this.maxAmount = i;
    }

    public void setRamInternally(int i) {
        this.ram = i;
    }

    public void setStaticInternally(boolean b) {
        this.isStatic = b;
    }

    public void setPriorityInternally(int value) {
        this.priority = value;
    }

    public void setBaseInternally(BaseObjectLink base) {
        this.base = base;
    }

    public void addServerInternally(ServerObjectLink server) {
        this.servers.add(server);
    }

    public void removeServerInternally(ServerObjectLink server) {
        this.servers.remove(server);
    }

    public void setJavaParametersInternally(List<String> javaParameters) {
        this.javaParameters = javaParameters;
    }

    public void setSpigotParametersInternally(List<String> spigotParameters) {
        this.spigotParameters = spigotParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerGroupObjectBasicImplementation that = (ServerGroupObjectBasicImplementation) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
