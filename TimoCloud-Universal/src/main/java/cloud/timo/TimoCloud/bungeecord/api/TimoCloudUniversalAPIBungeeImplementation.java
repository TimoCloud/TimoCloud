package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.core.objects.ServerGroup;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimoCloudUniversalAPIBungeeImplementation implements TimoCloudUniversalAPI {
    private ArrayList<ServerGroupObjectBungeeImplementation> groups = new ArrayList<>();

    public void setData(String json) {
        ArrayList groups = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) new JSONParser().parse(json);
        } catch (Exception e) {
            TimoCloudBungee.severe("Error while parsing JSON API data: '" + json + "'. Please report this!");
            e.printStackTrace();
            return;
        }
        try {
            for (Object object : jsonArray) {
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
                resolver.addMapping(ServerObject.class, ServerObjectBungeeImplementation.class);
                module.setAbstractTypes(resolver);

                objectMapper.registerModule(module);

                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                ServerGroupObjectBungeeImplementation groupObject = new ServerGroupObjectBungeeImplementation(objectMapper.readValue((String) object, ServerGroupObjectBasicImplementation.class));
                List<ServerObject> serverObjects = new ArrayList<>();
                for (ServerObject serverObject : groupObject.getServers())
                    serverObjects.add(new ServerObjectBungeeImplementation((ServerObjectBasicImplementation) serverObject));
                groupObject.getServers().clear();
                groupObject.getServers().addAll(serverObjects);
                groups.add(groupObject);
            }
            this.groups = groups;
        } catch (Exception e) {
            TimoCloudBungee.severe("&cError while creating objects from JSON API data: &e'" + json + "'&c. Please report this!");
            e.printStackTrace();
        }

    }

    @Override
    public List<ServerGroupObject> getServerGroups() {
        return groups == null ? null : (ArrayList) groups.clone();
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
        List<ServerGroupObject> groups = getServerGroups();
        if (groups == null) return null;
        for (ServerGroupObject group : groups)
            for (ServerObject server : group.getServers())
                if (server.getName().equals(serverName)) return server;
        for (ServerGroupObject group : groups)
            for (ServerObject server : group.getServers())
                if (server.getName().equalsIgnoreCase(serverName)) return server;
        return null;
    }
}
