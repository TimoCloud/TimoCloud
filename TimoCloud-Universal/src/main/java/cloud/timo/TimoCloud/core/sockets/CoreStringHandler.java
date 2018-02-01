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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

public class CoreStringHandler extends SimpleChannelInboundHandler<String> {

    private Map<Channel, Integer> open;
    private Map<Channel, StringBuilder> parsed;

    public CoreStringHandler() {
        open = new HashMap<>();
        parsed = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        try {
            read(ctx.channel(), message);
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while parsing JSON message: " + message);
            e.printStackTrace();
        }
    }

    public void read(Channel channel, String message) {
        for (String c : message.split("")) {
            if (c.equals("{")) open.put(channel, getOpen(channel) + 1);
            getParsed(channel).append(c);
            if (c.equals("}")) {
                open.put(channel, getOpen(channel) - 1);
                if (getOpen(channel) == 0) {
                    try {
                        handleJSON((JSONObject) JSONValue.parse(getParsed(channel).toString()), getParsed(channel).toString(), channel);
                    } catch (Exception e) {
                        TimoCloudCore.getInstance().severe("Error while parsing JSON message: " + getParsed(channel));
                        e.printStackTrace();
                    }
                    parsed.put(channel, new StringBuilder());
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
                if (!server.getToken().equals(data)) {
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
                if (!proxy.getToken().equals(data)) {
                    channel.close();
                    break;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, proxy);
                proxy.onConnect(channel);
                break;
            case "BASE_HANDSHAKE":
                InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
                if (!((List<String>) TimoCloudCore.getInstance().getFileManager().getConfig().get("allowedIPs")).contains(address.getHostAddress())) {
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
                    for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalInstance().getGroups())
                        groups.add(objectMapper.writeValueAsString(serverGroupObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, "APIDATA", groups.toJSONString());
                break;
            case "SERVER_TEMPLATE_REQUEST":
                JSONObject differences = (JSONObject) json.get("differences");
                List<String> templateDifferences = differences.containsKey("templateDifferences") ? (List<String>) differences.get("templateDifferences") : null;
                String template = json.containsKey("template") ? (String) json.get("template") : null;
                List<String> mapDifferences = differences.containsKey("mapDifferences") ? (List<String>) differences.get("mapDifferences") : null;
                String map = json.containsKey("map") ? (String) json.get("map") : null;
                List<String> globalDifferences = differences.containsKey("globalDifferences") ? (List<String>) differences.get("globalDifferences") : null;
                try {
                    if (templateDifferences != null) {
                        File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), template);
                        List<File> templateFiles = new ArrayList<>();
                        for (String fileName : templateDifferences) templateFiles.add(new File(templateDirectory, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, output);
                        channel.write(new ChunkedFile(output)).addListener(future -> {
                            output.delete();
                            Map<String, Object> msg = new HashMap<>();
                            msg.put("type", "TRANSFER_FINISHED");
                            msg.put("transferType", "SERVER_TEMPLATE");
                            msg.put("template", template);
                            channel.writeAndFlush(new JSONObject(msg));
                        });
                    }
                    if (mapDifferences != null) {
                        File mapDirecotry = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), map);
                        List<File> mapFiles = new ArrayList<>();
                        for (String fileName : mapDifferences) mapFiles.add(new File(mapDirecotry, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(mapFiles, output, "MAP_TEMPLATE", map);
                        channel.write(new ChunkedFile(output)).addListener(future -> {
                            output.delete();
                            Map<String, Object> msg = new HashMap<>();
                            msg.put("type", "TRANSFER_FINISHED");
                            msg.put("transferType", "SERVER_TEMPLATE");
                            msg.put("template", template);
                            channel.writeAndFlush(new JSONObject(msg));
                        });
                    }
                    if (globalDifferences != null) {
                        List<File> templateFiles = new ArrayList<>();
                        for (String fileName : templateDifferences) templateFiles.add(new File(TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory(), fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, output, "GLOBAL_TEMPLATE", template);
                        channel.write(new ChunkedFile(output)).addListener(future -> {
                            output.delete();
                            Map<String, Object> msg = new HashMap<>();
                            msg.put("type", "TRANSFER_FINISHED");
                            msg.put("transferType", "GLOBAL_SERVER_TEMPLATE");
                            channel.writeAndFlush(new JSONObject(msg));
                        });
                    }
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while sending template files: ");
                    e.printStackTrace();
                }
                break;
            default:
                target.onMessage(json);
        }
    }

    public int getOpen(Channel channel) {
        open.putIfAbsent(channel, 0);
        return open.get(channel);
    }

    public StringBuilder getParsed(Channel channel) {
        parsed.putIfAbsent(channel, new StringBuilder());
        return parsed.get(channel);
    }

}
