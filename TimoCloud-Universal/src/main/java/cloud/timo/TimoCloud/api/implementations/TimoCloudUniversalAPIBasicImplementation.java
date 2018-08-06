package cloud.timo.TimoCloud.api.implementations; // This relies on the jackson API, hence it has to be in the TimoCloud-Universal package

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.objects.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.*;
import java.util.stream.Collectors;

public class TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    private Set<ServerGroupObject> serverGroups = new HashSet<>();
    private Set<ProxyGroupObject> proxyGroups = new HashSet<>();
    private Set<BaseObject> bases = new HashSet<>();
    private Set<CordObject> cords = new HashSet<>();


    private final Class<? extends ServerObject> serverObjectImplementation;
    private final Class<? extends ProxyObject> proxyObjectImplementation;
    private final Class<? extends ServerGroupObject> serverGroupObjectImplementation;
    private final Class<? extends ProxyGroupObject> proxyGroupObjectImplementation;
    private final Class<? extends PlayerObject> playerObjectImplementation;
    private final Class<? extends BaseObject> baseObjectImplementation;
    private final Class<? extends CordObject> cordObjectImplementation;

    private boolean gotAnyData = false;

    private ObjectMapper objectMapper;

    public TimoCloudUniversalAPIBasicImplementation(Class<? extends ServerObject> serverObjectImplementation, Class<? extends ProxyObject> proxyObjectImplementation, Class<? extends ServerGroupObject> serverGroupObjectImplementation, Class<? extends ProxyGroupObject> proxyGroupObjectImplementation, Class<? extends PlayerObject> playerObjectImplementation, Class<? extends BaseObject> baseObjectImplementation, Class<? extends CordObject> cordObjectImplementation) {
        this.serverObjectImplementation = serverObjectImplementation;
        this.proxyObjectImplementation = proxyObjectImplementation;
        this.serverGroupObjectImplementation = serverGroupObjectImplementation;
        this.proxyGroupObjectImplementation = proxyGroupObjectImplementation;
        this.playerObjectImplementation = playerObjectImplementation;
        this.baseObjectImplementation = baseObjectImplementation;
        this.cordObjectImplementation = cordObjectImplementation;

        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(ServerObject.class, serverObjectImplementation);
        resolver.addMapping(ProxyObject.class, proxyObjectImplementation);
        resolver.addMapping(PlayerObject.class, playerObjectImplementation);
        resolver.addMapping(BaseObject.class, baseObjectImplementation);
        resolver.addMapping(CordObject.class, cordObjectImplementation);
        module.setAbstractTypes(resolver);
        objectMapper.registerModule(module);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public void setData(Map<String, Object> json) {
        Set<ServerGroupObject> serverGroups = new HashSet<>();
        Set<ProxyGroupObject> proxyGroups = new HashSet<>();
        Set<CordObject> cords = new HashSet<>();
        Set<BaseObject> bases = new HashSet<>();
        try {
            for (Object object : (List) json.get("serverGroups")) {
                ServerGroupObject groupObject = getObjectMapper().readValue((String) object, serverGroupObjectImplementation);
                serverGroups.add(groupObject);
            }
            this.serverGroups = serverGroups;
            for (Object object : (List) json.get("proxyGroups")) {
                ProxyGroupObject groupObject = getObjectMapper().readValue((String) object, proxyGroupObjectImplementation);
                proxyGroups.add(groupObject);
            }
            this.proxyGroups = proxyGroups;
            for (Object object : (List) json.get("cords")) {
                CordObject cordObject = getObjectMapper().readValue((String) object, cordObjectImplementation);
                cords.add(cordObject);
            }
            this.cords = cords;
            for (Object object : (List) json.get("bases")) {
                BaseObject baseObject = getObjectMapper().readValue((String) object, baseObjectImplementation);
                bases.add(baseObject);
            }
            this.bases = bases;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.gotAnyData = true;
    }

    @Override
    public Set<ServerGroupObject> getServerGroups() {
        return Collections.unmodifiableSet(serverGroups == null ? Collections.emptySet() : serverGroups);
    }

    @Override
    public ServerGroupObject getServerGroup(String groupName) {
        Set<ServerGroupObject> groups = getServerGroups();
        if (groups == null) return null;
        for (ServerGroupObject group : groups) if (group.getName().equals(groupName)) return group;
        for (ServerGroupObject group : groups) if (group.getName().equalsIgnoreCase(groupName)) return group;
        return null;
    }

    @Override
    public ServerObject getServer(String serverName) {
        for (ServerGroupObject group : serverGroups)
            for (ServerObject server : group.getServers())
                if (server.getName().equals(serverName) || server.getId().equals(serverName)) return server;
        for (ServerGroupObject group : serverGroups)
            for (ServerObject server : group.getServers())
                if (server.getName().equalsIgnoreCase(serverName)) return server;
        return null;
    }

    @Override
    public Set<ProxyGroupObject> getProxyGroups() {
        return Collections.unmodifiableSet(proxyGroups == null ? Collections.emptySet() : proxyGroups);
    }

    @Override
    public ProxyGroupObject getProxyGroup(String groupName) {
        for (ProxyGroupObject group : proxyGroups) if (group.getName().equals(groupName)) return group;
        for (ProxyGroupObject group : proxyGroups) if (group.getName().equalsIgnoreCase(groupName)) return group;
        return null;
    }

    @Override
    public ProxyObject getProxy(String proxyName) {
        for (ProxyGroupObject group : proxyGroups)
            for (ProxyObject proxy : group.getProxies())
                if (proxy.getName().equals(proxyName) || proxy.getId().equals(proxyName)) return proxy;
        for (ProxyGroupObject group : proxyGroups)
            for (ProxyObject proxy : group.getProxies())
                if (proxy.getName().equalsIgnoreCase(proxyName)) return proxy;
        return null;
    }

    @Override
    public PlayerObject getPlayer(UUID uuid) {
        for (ProxyObject proxyObject : getProxyGroups().stream().map(ProxyGroupObject::getProxies).flatMap(Collection::stream).collect(Collectors.toList()))
            for (PlayerObject playerObject : proxyObject.getOnlinePlayers())
                if (playerObject.getUuid().equals(uuid)) return playerObject;
        return null;
    }

    @Override
    public PlayerObject getPlayer(String name) {
        for (ProxyObject proxyObject : getProxyGroups().stream().map(ProxyGroupObject::getProxies).flatMap(Collection::stream).collect(Collectors.toList()))
            for (PlayerObject playerObject : proxyObject.getOnlinePlayers())
                if (playerObject.getName().equals(name)) return playerObject;
        return null;
    }

    @Override
    public Set<BaseObject> getBases() {
        return Collections.unmodifiableSet(bases == null ? Collections.emptySet() : bases);
    }

    @Override
    public BaseObject getBase(String name) {
        for (BaseObject baseObject : getBases())
            if (baseObject.getName().equals(name)) return baseObject;
        for (BaseObject baseObject : getBases())
            if (baseObject.getName().equalsIgnoreCase(name)) return baseObject;
        return new BaseObjectOfflineImplementation(name);
    }

    @Override
    public Set<CordObject> getCords() {
        return Collections.unmodifiableSet(cords == null ? Collections.emptySet() : cords);
    }

    @Override
    public CordObject getCord(String name) {
        for (CordObject cordObject : getCords())
            if (cordObject.getName().equals(name)) return cordObject;
        for (CordObject cordObject : getCords())
            if (cordObject.getName().equalsIgnoreCase(name)) return cordObject;
        return null;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public synchronized boolean gotAnyData() {
        return gotAnyData;
    }
}
