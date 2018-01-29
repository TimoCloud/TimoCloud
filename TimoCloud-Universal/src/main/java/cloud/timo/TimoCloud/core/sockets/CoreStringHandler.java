package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreStringHandler extends SimpleChannelInboundHandler<String> {

    private Map<Channel, Integer> open;
    private Map<Channel, String> remaining;
    private Map<Channel, String> parsed;

    public CoreStringHandler() {
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
            TimoCloudCore.getInstance().severe("Error while parsing JSON message(s): " + getRemaining(ctx.channel()));
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
                        TimoCloudCore.getInstance().severe("Error while parsing JSON message: " + getParsed(channel));
                        e.printStackTrace();
                    }
                    parsed.put(channel, "");
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message, Channel channel) {
        Server server = json.containsKey("server") ? TimoCloudCore.getInstance().getServerManager().getServerByName((String) json.get("server")) : null;
        Proxy proxy = json.containsKey("proxy") ? TimoCloudCore.getInstance().getServerManager().getProxyByName((String) json.get("proxy")) : null;
        String baseName = (String) json.get("base");
        Communicatable target = null;
        if (server != null) target = server;
        else if (proxy != null) target = proxy;
        else if (baseName != null) target = TimoCloudCore.getInstance().getServerManager().getBase(baseName);
        if (target == null) target = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        switch (type) {
            case "SERVER_HANDSHAKE":
                if (server == null) {
                    channel.close();
                    return;
                }
                if (! server.getToken().equals(data)) {
                    channel.close();
                    break;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, server);
                server.onConnect(channel);
                break;
            case "PROXY_HANDSHAKE":
                if (proxy == null) {
                    channel.close();
                    return;
                }
                if (! proxy.getToken().equals(data)) {
                    channel.close();
                    break;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, proxy);
                proxy.onConnect(channel);
                break;
            case "BASE_HANDSHAKE":
                InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
                if (! ((List<String>)TimoCloudCore.getInstance().getFileManager().getConfig().get("allowedIPs")).contains(address.getHostAddress())) {
                    TimoCloudCore.getInstance().severe("Unknown base connected from " + address.getHostAddress() + ". If you want to allow this connection, please add the IP address to 'allowedIPs' in your config.yml, else, please block the port " + ((Integer) TimoCloudCore.getInstance().getFileManager().getConfig().get("socket-port")) + " in your firewall.");
                    channel.close();
                    return;
                }
                Base base = TimoCloudCore.getInstance().getServerManager().getBase(baseName, address, channel);
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, base);
                break;
            case "GET_API_DATA":
                JSONArray groups = new JSONArray();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                try {
                    for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalInstance().getGroups()) groups.add(objectMapper.writeValueAsString(serverGroupObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, "APIDATA", groups.toJSONString());
                break;
            default:
                target.onMessage(json);
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
