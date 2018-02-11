package cloud.timo.TimoCloud.implementations;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    private ArrayList<ServerGroupObject> serverGroups = new ArrayList<>();
    private ArrayList<ProxyGroupObject> proxyGroups = new ArrayList<>();

    private final Class<? extends ServerObject> serverObjectImplementation;
    private final Class<? extends ProxyObject> proxyObjectImplementation;
    private final Class<? extends ServerGroupObject> serverGroupObjectImplementation;
    private final Class<? extends ProxyGroupObject> proxyGroupObjectImplementation;

    public TimoCloudUniversalAPIBasicImplementation(Class<? extends ServerObject> serverObjectImplementation, Class<? extends ProxyObject> proxyObjectImplementation, Class<? extends ServerGroupObject> serverGroupObjectImplementation, Class<? extends ProxyGroupObject> proxyGroupObjectImplementation) {
        this.serverObjectImplementation = serverObjectImplementation;
        this.proxyObjectImplementation = proxyObjectImplementation;
        this.serverGroupObjectImplementation = serverGroupObjectImplementation;
        this.proxyGroupObjectImplementation = proxyGroupObjectImplementation;
    }

    public void setData(JSONObject json) {
        ArrayList serverGroups = new ArrayList<>();
        ArrayList proxyGroups = new ArrayList();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
            resolver.addMapping(ServerObject.class, serverObjectImplementation);
            resolver.addMapping(ProxyObject.class, proxyObjectImplementation);
            module.setAbstractTypes(resolver);
            objectMapper.registerModule(module);
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            for (Object object : (JSONArray) json.get("serverGroups")) {
                ServerGroupObject groupObject = objectMapper.readValue((String) object, serverGroupObjectImplementation);
                List<ServerObject> serverObjects = new ArrayList<>();
                for (ServerObject serverObject : groupObject.getServers())
                    serverObjects.add(serverObject);
                groupObject.getServers().clear();
                groupObject.getServers().addAll(serverObjects);
                serverGroups.add(groupObject);
            }
            this.serverGroups = serverGroups;
            for (Object object : (JSONArray) json.get("proxyGroups")) {
                ProxyGroupObject groupObject = objectMapper.readValue((String) object, proxyGroupObjectImplementation);
                List<ProxyObject> proxyObjects = new ArrayList<>();
                for (ProxyObject proxyObject : groupObject.getProxies())
                    proxyObjects.add(proxyObject);
                groupObject.getProxies().clear();
                groupObject.getProxies().addAll(proxyObjects);
                proxyGroups.add(groupObject);
            }
            this.proxyGroups = proxyGroups;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<ServerGroupObject> getServerGroups() {
        return serverGroups == null ? new ArrayList<>() : (ArrayList) serverGroups.clone();
    }

    @Override
    public ServerGroupObject getServerGroup(String groupName) {
        List<ServerGroupObject> groups = getServerGroups();
        if (groups == null) return null;
        for (ServerGroupObject group : groups) if (group.getName().equals(groupName)) return group;
        for (ServerGroupObject group : groups) if (group.getName().equalsIgnoreCase(groupName)) return group;
        return null;
    }

    @Override
    public ServerObject getServer(String serverName) {
        for (ServerGroupObject group : serverGroups)
            for (ServerObject server : group.getServers())
                if (server.getName().equals(serverName)) return server;
        for (ServerGroupObject group : serverGroups)
            for (ServerObject server : group.getServers())
                if (server.getName().equalsIgnoreCase(serverName)) return server;
        return null;
    }

    @Override
    public List<ProxyGroupObject> getProxyGroups() {
        return serverGroups == null ? new ArrayList<>() : (ArrayList) proxyGroups.clone();
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
                if (proxy.getName().equals(proxyName)) return proxy;
        for (ProxyGroupObject group : proxyGroups)
            for (ProxyObject proxy : group.getProxies())
                if (proxy.getName().equalsIgnoreCase(proxyName)) return proxy;
        return null;
    }

}
