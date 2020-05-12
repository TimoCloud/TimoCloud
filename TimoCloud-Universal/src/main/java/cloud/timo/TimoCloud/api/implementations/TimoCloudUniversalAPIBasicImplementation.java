package cloud.timo.TimoCloud.api.implementations; // This relies on the jackson API, hence it has to be in the TimoCloud-Universal package

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.implementations.listeners.TimoCloudUniversalAPIStorageUpdateListener;
import cloud.timo.TimoCloud.api.implementations.storage.IdentifiableObjectStorage;
import cloud.timo.TimoCloud.api.objects.*;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.*;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

public class TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    private IdentifiableObjectStorage<ServerGroupObject> serverGroups = new IdentifiableObjectStorage<>();
    private IdentifiableObjectStorage<ProxyGroupObject> proxyGroups = new IdentifiableObjectStorage<>();
    private IdentifiableObjectStorage<ServerObject> servers = new IdentifiableObjectStorage<>();
    private IdentifiableObjectStorage<ProxyObject> proxies = new IdentifiableObjectStorage<>();
    private IdentifiableObjectStorage<BaseObject> bases = new IdentifiableObjectStorage<>();
    private IdentifiableObjectStorage<PlayerObject> players = new IdentifiableObjectStorage<>();
    private IdentifiableObjectStorage<CordObject> cords = new IdentifiableObjectStorage<>();

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

        this.objectMapper = prepareObjectMapper();
    }

    private ObjectMapper prepareObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(ServerGroupObject.class, serverGroupObjectImplementation);
        resolver.addMapping(ProxyGroupObject.class, proxyGroupObjectImplementation);
        resolver.addMapping(ServerObject.class, serverObjectImplementation);
        resolver.addMapping(ProxyObject.class, proxyObjectImplementation);
        resolver.addMapping(PlayerObject.class, playerObjectImplementation);
        resolver.addMapping(BaseObject.class, baseObjectImplementation);
        resolver.addMapping(CordObject.class, cordObjectImplementation);

        for (Class<? extends Event> eventClass : EventUtil.getEventClassImplementations().keySet()) {
            resolver.addMapping((Class) eventClass, EventUtil.getEventClassImplementation(eventClass));
        }

        module.setAbstractTypes(resolver);
        objectMapper.registerModule(module);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        return objectMapper;
    }

    public void setData(Map<String, Object> json) {
        try {
            ((Collection) json.get("serverGroups")).stream()
                    .map(object -> readValue((String) object, serverGroupObjectImplementation))
                    .forEach(serverGroup -> this.serverGroups.add((ServerGroupObject) serverGroup));
            ((Collection) json.get("proxyGroups")).stream()
                    .map(object -> readValue((String) object, proxyGroupObjectImplementation))
                    .forEach(proxyGroup -> this.proxyGroups.add((ProxyGroupObject) proxyGroup));
            ((Collection) json.get("servers")).stream()
                    .map(object -> readValue((String) object, serverObjectImplementation))
                    .forEach(server -> this.servers.add((ServerObject) server));
            ((Collection) json.get("proxies")).stream()
                    .map(object -> readValue((String) object, proxyObjectImplementation))
                    .forEach(proxy -> this.proxies.add((ProxyObject) proxy));
            ((Collection) json.get("bases")).stream()
                    .map(object -> readValue((String) object, baseObjectImplementation))
                    .forEach(base -> this.bases.add((BaseObject) base));
            ((Collection) json.get("players")).stream()
                    .map(object -> readValue((String) object, playerObjectImplementation))
                    .forEach(player -> this.players.add((PlayerObject) player));
            ((Collection) json.get("cords")).stream()
                    .map(object -> readValue((String) object, cordObjectImplementation))
                    .forEach(cord -> this.cords.add((CordObject) cord));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (! this.gotAnyData) {
            TimoCloudAPI.getEventAPI().registerListener(new TimoCloudUniversalAPIStorageUpdateListener(this));
        }
        this.gotAnyData = true;
    }

    @Override
    public Set<ServerGroupObject> getServerGroups() {
        return Collections.unmodifiableSet(new HashSet<>(serverGroups.values()));
    }

    @Override
    public ServerGroupObject getServerGroup(String identifier) {
        return serverGroups.getByIdentifier(identifier);
    }

    @Override
    public ServerObject getServer(String identifier) {
        return servers.getByIdentifier(identifier);
    }

    @Override
    public Collection<ServerObject> getServers() {
        return servers.values();
    }

    @Override
    public Set<ProxyGroupObject> getProxyGroups() {
        return Collections.unmodifiableSet(new HashSet<>(proxyGroups.values()));
    }

    @Override
    public ProxyGroupObject getProxyGroup(String identifier) {
        return proxyGroups.getByIdentifier(identifier);
    }

    @Override
    public ProxyObject getProxy(String identifier) {
        return proxies.getByIdentifier(identifier);
    }

    @Override
    public Collection<ProxyObject> getProxies() {
        return proxies.values();
    }

    @Override
    public PlayerObject getPlayer(UUID uuid) {
        return players.getById(uuid.toString());
    }

    @Override
    public PlayerObject getPlayer(String name) {
        return players.getByName(name);
    }

    @Override
    public Collection<PlayerObject> getPlayers() {
        return players.values();
    }

    @Override
    public APIRequestFuture<Void> registerBase(Base64 publickey) {
        return new APIRequestImplementation<Void>(G_REGISTER_PUBLICKEY, "core", publickey).submit();
    }


    @Override
    public Collection<BaseObject> getBases() {
        return Collections.unmodifiableSet(new HashSet<>(bases.values()));
    }

    @Override
    public BaseObject getBase(String identifier) {
        return bases.getByIdentifier(identifier);
    }

    @Override
    public Collection<CordObject> getCords() {
        return Collections.unmodifiableSet(new HashSet<>(cords.values()));
    }

    @Override
    public CordObject getCord(String identifier) {
        return cords.getByIdentifier(identifier);
    }

    @Override
    public APIRequestFuture<ServerGroupObject> createServerGroup(ServerGroupProperties properties) {
        return new APIRequestImplementation<ServerGroupObject>(G_CREATE_SERVER_GROUP, properties).submit();
    }

    @Override
    public APIRequestFuture<ProxyGroupObject> createProxyGroup(ProxyGroupProperties properties) {
        return new APIRequestImplementation<ProxyGroupObject>(G_CREATE_PROXY_GROUP, properties).submit();
    }

    public IdentifiableObjectStorage<ServerGroupObject> getServerGroupStorage() {
        return serverGroups;
    }

    public IdentifiableObjectStorage<ProxyGroupObject> getProxyGroupStorage() {
        return proxyGroups;
    }

    public IdentifiableObjectStorage<ServerObject> getServerStorage() {
        return servers;
    }

    public IdentifiableObjectStorage<ProxyObject> getProxyStorage() {
        return proxies;
    }

    public IdentifiableObjectStorage<BaseObject> getBaseStorage() {
        return bases;
    }

    public IdentifiableObjectStorage<PlayerObject> getPlayerStorage() {
        return players;
    }

    public IdentifiableObjectStorage<CordObject> getCordStorage() {
        return cords;
    }

    private <T> T readValue(String input, Class<? extends T> clazz) {
        try {
            return getObjectMapper().readValue(input, clazz);
        } catch (Exception e) {
            TimoCloudLogger.getLogger().severe(e);
            return null;
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public synchronized boolean gotAnyData() {
        return gotAnyData;
    }
}
