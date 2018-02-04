package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class TimoCloudUniversalAPIBukkitImplementation implements TimoCloudUniversalAPI {

    private ArrayList<ServerGroupObjectBukkitImplementation> groups = new ArrayList<>();

    public void setData(String json) {
        ArrayList groups = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) new JSONParser().parse(json);
        } catch (Exception e) {
            TimoCloudBukkit.log("&cError while parsing JSON API data: &e'" + json + "'&c. Please report this!");
            e.printStackTrace();
            return;
        }
        try {
            for (Object object : jsonArray) {
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
                resolver.addMapping(ServerObject.class, ServerObjectBukkitImplementation.class);
                module.setAbstractTypes(resolver);

                objectMapper.registerModule(module);

                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                ServerGroupObjectBukkitImplementation groupObject = new ServerGroupObjectBukkitImplementation(objectMapper.readValue((String) object, ServerGroupObjectBasicImplementation.class));
                List<ServerObject> serverObjects = new ArrayList<>();
                for (ServerObject serverObject : groupObject.getServers())
                    serverObjects.add(new ServerObjectBukkitImplementation((ServerObjectBasicImplementation) serverObject));
                groupObject.getServers().clear();
                groupObject.getServers().addAll(serverObjects);
                groups.add(groupObject);
            }
            this.groups = groups;
        } catch (Exception e) {
            TimoCloudBukkit.log("&cError while creating objects from JSON API data: &e'" + json + "'&c. Please report this!");
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
