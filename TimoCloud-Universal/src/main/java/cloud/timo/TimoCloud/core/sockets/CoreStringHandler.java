package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.utils.DoAfterAmount;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
        String targetName = (String) json.get("target");
        Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(targetName);
        Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(targetName);
        String baseName = (String) json.get("base");
        Communicatable target = null;
        if (server != null) target = server;
        else if (proxy != null) target = proxy;
        else if (baseName != null) target = TimoCloudCore.getInstance().getServerManager().getBase(baseName);
        if (target == null) target = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String type = (String) json.get("type");
        Object data = json.get("data");
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
                base.onConnect(channel);
                break;
            case "GET_API_DATA":
                JSONArray groups = new JSONArray();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                try {
                    for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalInstance().getServerGroups())
                        groups.add(objectMapper.writeValueAsString(serverGroupObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, "APIDATA", groups.toJSONString());
                break;
            case "SERVER_TEMPLATE_REQUEST": {
                if (server != null)
                    server.getBase().setAvailableRam(server.getBase().getAvailableRam() + server.getGroup().getRam()); // Start paused, hence ram is free
                JSONObject differences = (JSONObject) json.get("differences");
                List<String> templateDifferences = differences.containsKey("templateDifferences") ? (List<String>) differences.get("templateDifferences") : null;
                String template = json.containsKey("template") ? (String) json.get("template") : null;
                List<String> mapDifferences = differences.containsKey("mapDifferences") ? (List<String>) differences.get("mapDifferences") : null;
                String map = json.containsKey("map") ? (String) json.get("map") : null;
                List<String> globalDifferences = differences.containsKey("globalDifferences") ? (List<String>) differences.get("globalDifferences") : null;
                int amount = 0;
                if (templateDifferences != null) amount++;
                if (mapDifferences != null) amount++;
                if (globalDifferences != null) amount++;
                DoAfterAmount doAfterAmount = new DoAfterAmount(amount, server::start);
                try {
                    if (templateDifferences != null) {
                        File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), template);
                        List<File> templateFiles = new ArrayList<>();
                        for (String fileName : templateDifferences)
                            templateFiles.add(new File(templateDirectory, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, output);
                        String content = fileToString(output);
                        output.delete();
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("type", "TRANSFER");
                        msg.put("transferType", "SERVER_TEMPLATE");
                        msg.put("template", template);
                        msg.put("file", content);
                        channel.writeAndFlush(new JSONObject(msg).toString());
                        doAfterAmount.addOne();
                    }
                    if (mapDifferences != null) {
                        File mapDirecotry = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), map);
                        List<File> mapFiles = new ArrayList<>();
                        for (String fileName : mapDifferences) mapFiles.add(new File(mapDirecotry, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(mapFiles, mapDirecotry, output);
                        String content = fileToString(output);
                        output.delete();
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("type", "TRANSFER_FINISHED");
                        msg.put("transferType", "SERVER_TEMPLATE");
                        msg.put("template", template);
                        msg.put("file", content);
                        channel.writeAndFlush(new JSONObject(msg).toString());
                        doAfterAmount.addOne();
                    }
                    if (globalDifferences != null) {
                        List<File> templateFiles = new ArrayList<>();
                        File templateDirectory = TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory();
                        for (String fileName : templateDifferences)
                            templateFiles.add(new File(templateDirectory, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, output);
                        String content = fileToString(output);
                        output.delete();
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("type", "TRANSFER_FINISHED");
                        msg.put("transferType", "SERVER_GLOBAL_TEMPLATE");
                        msg.put("file", content);
                        channel.writeAndFlush(new JSONObject(msg).toString());
                        doAfterAmount.addOne();
                    }
                    doAfterAmount.setAmount(amount);
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while sending template files: ");
                    e.printStackTrace();
                }
                break;
            }
            case "PROXY_TEMPLATE_REQUEST": {
                if (proxy != null)
                    proxy.getBase().setAvailableRam(proxy.getBase().getAvailableRam() + proxy.getGroup().getRam()); // Start paused, hence ram is free
                JSONObject differences = (JSONObject) json.get("differences");
                List<String> templateDifferences = differences.containsKey("templateDifferences") ? (List<String>) differences.get("templateDifferences") : null;
                String template = json.containsKey("template") ? (String) json.get("template") : null;
                List<String> globalDifferences = differences.containsKey("globalDifferences") ? (List<String>) differences.get("globalDifferences") : null;
                int amount = 0;
                if (templateDifferences != null) amount++;
                if (globalDifferences != null) amount++;
                DoAfterAmount doAfterAmount = new DoAfterAmount(amount, proxy::start);
                try {
                    if (templateDifferences != null) {
                        File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getProxyTemplatesDirectory(), template);
                        List<File> templateFiles = new ArrayList<>();
                        for (String fileName : templateDifferences)
                            templateFiles.add(new File(templateDirectory, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, output);
                        String content = fileToString(output);
                        output.delete();
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("type", "TRANSFER");
                        msg.put("transferType", "PROXY_TEMPLATE");
                        msg.put("template", template);
                        msg.put("file", JSONObject.escape(content));
                        channel.writeAndFlush(new JSONObject(msg).toString());
                        doAfterAmount.addOne();
                    }
                    if (globalDifferences != null) {
                        List<File> templateFiles = new ArrayList<>();
                        File templateDirectory = TimoCloudCore.getInstance().getFileManager().getProxyGlobalDirectory();
                        for (String fileName : globalDifferences)
                            templateFiles.add(new File(templateDirectory, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, output);
                        String content = fileToString(output);
                        output.delete();
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("type", "TRANSFER");
                        msg.put("transferType", "PROXY_GLOBAL_TEMPLATE");
                        msg.put("file", content);
                        channel.writeAndFlush(new JSONObject(msg).toString());
                        doAfterAmount.addOne();
                    }
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while sending template files: ");
                    e.printStackTrace();
                }
                break;
            }
            default:
                target.onMessage(json);
        }
    }

    public String fileToString(File file) throws Exception {
        byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
        return new String(encoded, StandardCharsets.UTF_8);
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
