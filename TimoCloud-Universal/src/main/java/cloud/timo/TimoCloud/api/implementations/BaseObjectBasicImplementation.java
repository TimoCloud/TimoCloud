package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseObjectBasicImplementation implements BaseObject {

    private String name;
    private InetAddress ipAddress;
    private Double cpuLoad;
    private int freeRam;
    private int maxRam;
    private boolean connected;
    private boolean ready;
    private Set<String> servers;
    private Set<String> proxies;

    public BaseObjectBasicImplementation() {}

    public BaseObjectBasicImplementation(String name, InetAddress ipAddress, Double cpuLoad, int freeRam, int maxRam, Boolean connected, Boolean ready, Set<String> servers, Set<String> proxies) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.cpuLoad = cpuLoad;
        this.freeRam = freeRam;
        this.maxRam = maxRam;
        this.connected = connected;
        this.ready = ready;
        this.servers = servers;
        this.proxies = proxies;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    @Override
    public Double getCpuLoad() {
        return cpuLoad;
    }

    @Override
    public int getAvailableRam() {
        return freeRam;
    }

    @Override
    public int getMaxRam() {
        return maxRam;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public Set<ServerObject> getServers() {
        return servers.stream().map(name -> TimoCloudAPI.getUniversalAPI().getServer(name)).collect(Collectors.toSet());
    }

    @Override
    public Set<ProxyObject> getProxies() {
        return proxies.stream().map(name -> TimoCloudAPI.getUniversalAPI().getProxy(name)).collect(Collectors.toSet());
    }
}
