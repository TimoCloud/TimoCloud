package at.TimoCraft.TimoCloud.bungeecord.sockets;

import at.TimoCraft.TimoCloud.api.TimoCloudAPI;
import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.BaseObject;
import at.TimoCraft.TimoCloud.bungeecord.objects.Group;
import at.TimoCraft.TimoCloud.bungeecord.objects.Server;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BungeeStringHandler extends SimpleChannelInboundHandler<String> {

    private Map<Channel, Integer> open;
    private Map<Channel, String> remaining;
    private Map<Channel, String> parsed;

    public BungeeStringHandler() {
        open = new HashMap<>();
        remaining = new HashMap<>();
        parsed = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        try {
            remaining.put(ctx.channel(), getRemaining(ctx.channel()) + message);
            read(ctx.channel());
        } catch (Exception e) {
            TimoCloud.severe("Error while parsing JSON message(s): " + getRemaining(ctx.channel()));
            e.printStackTrace();
        }
    }

    public void read(Channel channel) {
        for (String c : getRemaining(channel).split("")) {
            if (c.equals("{")) open.put(channel, getOpen(channel) + 1);
            if (getOpen(channel) > 0) {
                parsed.put(channel, getParsed(channel) + c);
                remaining.put(channel, getRemaining(channel).substring(1));
            }
            if (c.equals("}")) {
                open.put(channel, getOpen(channel) - 1);
                if (getOpen(channel) == 0) {
                    try {
                        handleJSON((JSONObject) JSONValue.parse(getParsed(channel)), getParsed(channel), channel);
                    } catch (Exception e) {
                        TimoCloud.severe("Error while parsing JSON message: " + getParsed(channel));
                        e.printStackTrace();
                    }
                    parsed.put(channel, "");
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message, Channel channel) {
        String serverName = (String) json.get("server");
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        Server server = null;
        Server requestedServer = null;
        if (! type.toLowerCase().startsWith("base")) {
            server = TimoCloud.getInstance().getServerManager().getServerByName(serverName);
            requestedServer = TimoCloud.getInstance().getServerManager().getServerByName(data);
        }
        switch (type) {
            case "HANDSHAKE":
                if (! data.equals(server.getToken())) {
                    channel.close();
                    break;
                }
                server.onConnect(channel);
                break;
            case "BASE_HANDSHAKE":
                InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
                if (!TimoCloud.getInstance().getFileManager().getConfig().getStringList("allowedIPs").contains(address.getHostAddress())) {
                    TimoCloud.severe("Unknown base connected from " + address.getHostAddress() + ". If you want to allow this connection, please add the IP address to 'allowedIPs' in your config.yml, else, please block the port " + TimoCloud.getInstance().getFileManager().getConfig().getInt("socket-port") + " in your firewall.");
                    channel.close();
                    return;
                }
                if (TimoCloud.getInstance().getServerManager().getBase(serverName) != null) {
                    return;
                }
                BaseObject base = new BaseObject(serverName, address, channel);
                TimoCloud.getInstance().getSocketServerHandler().getBaseChannels().put(channel, base);
                TimoCloud.getInstance().getServerManager().addBase(serverName, base);
                break;
            case "REGISTER":
                if (! data.equals(server.getToken())) {
                    channel.close();
                    break;
                }
                server.register();
                break;
            case "GET_API_DATA":
                JSONArray groups = new JSONArray();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                try {
                    for (GroupObject groupObject : TimoCloudAPI.getUniversalInstance().getGroups()) groups.add(objectMapper.writeValueAsString(groupObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, "APIDATA", groups.toJSONString());
                break;
            case "SET_STATE":
                server.setState(data);
                break;
            case "GET_STATE":
                String state = "OFFLINE";
                if (requestedServer != null) {
                    state = requestedServer.getState();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "STATE", state);
                break;
            case "SET_EXTRA":
                server.setExtra(data);
                break;
            case "GET_EXTRA":
                String extra = "";
                if (requestedServer != null) {
                    extra = requestedServer.getExtra();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "EXTRA", extra);
                break;
            case "SET_MOTD":
                server.setMotd(data);
                break;
            case "GET_MOTD":
                String motd = "";
                if (requestedServer != null) {
                    motd = requestedServer.getMotd();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "MOTD", motd);
                break;
            case "SET_MAP":
                server.setMap(data);
                break;
            case "GET_MAP":
                String map = "";
                if (requestedServer != null) {
                    map = requestedServer.getMap();
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "MAP", map);
                break;
            case "SET_PLAYERS":
                server.setCurrentPlayers(Integer.parseInt(data.split("/")[0]));
                server.setMaxPlayers(Integer.parseInt(data.split("/")[1]));
                break;
            case "GET_PLAYERS":
                Server requestedServer3 = TimoCloud.getInstance().getServerManager().getServerByName(data);
                if (requestedServer3 == null) {
                    return;
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "PLAYERS", requestedServer.getCurrentPlayers() + "/" + requestedServer.getMaxPlayers());
                break;
            case "GET_SERVERS":
                Group requestedGroup = TimoCloud.getInstance().getServerManager().getGroupByName(data);
                List<String> servers = new ArrayList<>();
                for (Server t : requestedGroup.getServers()) {
                    servers.add(t.getName());
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "SERVERS", servers);
                break;
            case "EXECUTE_COMMAND":
                TimoCloud.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloud.getInstance().getProxy().getConsole(), data);
                break;
            default:
                TimoCloud.severe("Could not categorize json message: " + message);
        }
    }

    public int getOpen(Channel channel) {
        open.putIfAbsent(channel, 0);
        return open.get(channel);
    }

    public String getRemaining(Channel channel) {
        remaining.putIfAbsent(channel, "");
        return remaining.get(channel);
    }

    public String getParsed(Channel channel) {
        parsed.putIfAbsent(channel, "");
        return parsed.get(channel);
    }

}
