package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.Collection;
import java.util.Set;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

public class ServerGroupObjectBasicImplementation implements ServerGroupObject {

    private Set<ServerObject> servers;
    private String name;
    private int startupAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private String base;
    private Set<String> sortOutStates;

    /**
     * Do not use this - this will be done by TimoCloud
     */
    public ServerGroupObjectBasicImplementation() {
    }

    /**
     * Do not use this - this will be done by TimoCloud
     */
    public ServerGroupObjectBasicImplementation(String name, Set<ServerObject> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, Set<String> sortOutStates) {
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
        return servers;
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
    public APIRequestFuture setOnlineAmount(int value) {
        return new APIRequestImplementation(SG_SET_ONLINE_AMOUNT, getName(), value).submit();
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public APIRequestFuture setMaxAmount(int value) {
        return new APIRequestImplementation(SG_SET_MAX_AMOUNT, getName(), value).submit();
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public APIRequestFuture setRam(int value) {
        return new APIRequestImplementation(SG_SET_RAM, getName(), value).submit();
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public APIRequestFuture setStatic(boolean value) {
        return new APIRequestImplementation(SG_SET_STATIC, getName(), value).submit();
    }

    @Override
    public BaseObject getBase() {
        return TimoCloudAPI.getUniversalAPI().getBase(base);
    }

    @Override
    public APIRequestFuture setBase(BaseObject value) {
        return new APIRequestImplementation(SG_SET_BASE, getName(), value).submit();
    }

    @Override
    public Collection<String> getSortOutStates() {
        return sortOutStates;
    }

    @Override
    public APIRequestFuture setSortOutStates(Collection<String> value) {
        return new APIRequestImplementation(SG_SET_SORT_OUT_STATES, getName(), value).submit();
    }

}
