package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.internal.links.LinkableObject;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class BaseObjectBasicImplementation implements BaseObject, LinkableObject<BaseObject> {

    private String id;
    private String name;
    private InetAddress ipAddress;
    private Double cpuLoad;
    private Double maxCpuLoad;
    private int availableRam;
    private int maxRam;
    private boolean connected;
    private boolean ready;
    private Set<ServerObjectLink> servers;
    private Set<ProxyObjectLink> proxies;

    public BaseObjectBasicImplementation(String id, String name, InetAddress ipAddress, Double cpuLoad, Double maxCpuLoad, int availableRam, int maxRam, Boolean connected, Boolean ready, Set<ServerObjectLink> servers, Set<ProxyObjectLink> proxies) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.cpuLoad = cpuLoad;
        this.maxCpuLoad = maxCpuLoad;
        this.availableRam = availableRam;
        this.maxRam = maxRam;
        this.connected = connected;
        this.ready = ready;
        this.servers = servers;
        this.proxies = proxies;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public Double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(Double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    @Override
    public Double getMaxCpuLoad() {
        return maxCpuLoad;
    }

    public void setMaxCpuLoad(Double maxCpuLoad){
        this.maxCpuLoad = maxCpuLoad;
    }


    @Override
    public int getAvailableRam() {
        return availableRam;
    }

    public void setAvailableRam(int availableRam) {
        this.availableRam = availableRam;
    }



    @Override
    public int getMaxRam() {
        return maxRam;
    }

    public void setMaxRam(int maxRam) {
        this.maxRam = maxRam;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public Set<ServerObject> getServers() {
        return Collections.unmodifiableSet(servers.stream().map(ServerObjectLink::resolve).collect(Collectors.toSet()));
    }

    public void addServer(ServerObject serverObject) {
        servers.add(((ServerObjectBasicImplementation) serverObject).toLink());
    }

    public void removeServer(ServerObject serverObject) {
        servers.remove(((ServerObjectBasicImplementation) serverObject).toLink());
    }

    @Override
    public Set<ProxyObject> getProxies() {
        return Collections.unmodifiableSet(proxies.stream().map(ProxyObjectLink::resolve).collect(Collectors.toSet()));
    }

    public void addProxy(ProxyObject proxyObject) {
        proxies.add(((ProxyObjectBasicImplementation) proxyObject).toLink());
    }

    public void removeProxy(ProxyObject proxyObject) {
        proxies.remove(((ProxyObjectBasicImplementation) proxyObject).toLink());
    }

    @Override
    public BaseObjectLink toLink() {
        return new BaseObjectLink(this);
    }
}
