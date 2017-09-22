package at.TimoCraft.TimoCloud.bukkit.api;

import at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI;
import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObjectBasicImplementation;
import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class TimoCloudUniversalAPIBukkitImplementation implements TimoCloudUniversalAPI {

    private ArrayList<GroupObject> groups;

    public void setData(String json) {
        //System.out.println("Json: " + json);
        groups = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) new JSONParser().parse(json);
        } catch (Exception e) {
            TimoCloudBukkit.log("&cError while parsing JSON API data: &e'" + json + "'&c. Please report this!");
            e.printStackTrace();
            return;
        }
        for (Object object : jsonArray) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule module = new SimpleModule("CustomModel", Version.unknownVersion());

                SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
                resolver.addMapping(ServerObject.class, ServerObjectBukkitImplementation.class);

                module.setAbstractTypes(resolver);

                objectMapper.registerModule(module);

                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

                GroupObject groupObject = objectMapper.readValue((String) object, GroupObject.class);

                List<ServerObject> serverObjects = new ArrayList<>();
                for (ServerObject serverObject : groupObject.getStartingServers())
                    serverObjects.add(new ServerObjectBukkitImplementation((ServerObjectBasicImplementation) serverObject));
                groupObject.getStartingServers().clear();
                groupObject.getStartingServers().addAll(serverObjects);

                serverObjects = new ArrayList<>();
                for (ServerObject serverObject : groupObject.getRunningServers())
                    serverObjects.add(new ServerObjectBukkitImplementation((ServerObjectBasicImplementation) serverObject));
                groupObject.getRunningServers().clear();
                groupObject.getRunningServers().addAll(serverObjects);
                groups.add(groupObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        for (GroupObject group : groups) for (ServerObject server : group.getAllServers()) if (server.getName().equals(serverName)) return server;
        for (GroupObject group : groups) for (ServerObject server : group.getAllServers()) if (server.getName().equalsIgnoreCase(serverName)) return server;
        return null;
    }
}
