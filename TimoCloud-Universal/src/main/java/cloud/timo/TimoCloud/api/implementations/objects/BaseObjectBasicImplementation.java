package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.internal.links.LinkableObject;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class BaseObjectBasicImplementation implements BaseObject, LinkableObject<BaseObject> {

    @Getter
    private String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private InetAddress ipAddress;
    @Getter
    @Setter
    private Double cpuLoad;
    @Getter
    @Setter
    private Double maxCpuLoad;
    @Getter
    @Setter
    private int availableRam;
    @Getter
    @Setter
    private int maxRam;
    @Getter
    @Setter
    private boolean connected;
    @Getter
    @Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseObjectBasicImplementation that = (BaseObjectBasicImplementation) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
