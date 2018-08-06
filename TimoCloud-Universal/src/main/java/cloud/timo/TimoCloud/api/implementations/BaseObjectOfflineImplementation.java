package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;

public class BaseObjectOfflineImplementation implements BaseObject {

    private String name;

    public BaseObjectOfflineImplementation(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InetAddress getIpAddress() {
        return null;
    }

    @Override
    public Double getCpuLoad() {
        return 0.0;
    }

    @Override
    public int getAvailableRam() {
        return 0;
    }

    @Override
    public int getMaxRam() {
        return 0;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public Collection<ServerObject> getServers() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ProxyObject> getProxies() {
        return Collections.emptySet();
    }
}
