package at.TimoCraft.TimoCloud.bukkit.api;

import at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI;
import at.TimoCraft.TimoCloud.api.implementations.GroupObjectBasicImplementation;
import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
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

    private ArrayList<GroupObjectBukkitImplementation> groups = new ArrayList<>();

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
                GroupObjectBukkitImplementation groupObject = new GroupObjectBukkitImplementation(objectMapper.readValue((String) object, GroupObjectBasicImplementation.class));
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
    public List<GroupObject> getGroups() {
        return groups == null ? null : (ArrayList) groups.clone();
    }

    @Override
    public GroupObject getGroup(String groupName) {
        List<GroupObject> groups = getGroups();
        if (groups == null) return null;
        for (GroupObject group : groups) if (group.getName().equals(groupName)) return group;
        for (GroupObject group : groups) if (group.getName().equalsIgnoreCase(groupName)) return group;
        return null;
    }

    @Override
    public ServerObject getServer(String serverName) {
        List<GroupObject> groups = getGroups();
        if (groups == null) return null;
        for (GroupObject group : groups)
            for (ServerObject server : group.getServers())
                if (server.getName().equals(serverName)) return server;
        for (GroupObject group : groups)
            for (ServerObject server : group.getServers())
                if (server.getName().equalsIgnoreCase(serverName)) return server;
        return null;
    }
}
