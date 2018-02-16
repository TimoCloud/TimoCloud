package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.utils.DoAfterAmount;
import cloud.timo.TimoCloud.utils.EnumUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ChannelHandler.Sharable
public class CoreStringHandler extends BasicStringHandler {

    @Override
    public void handleJSON(JSONObject json, String message, Channel channel) {
        Communicatable sender = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String targetToken = (String) json.get("target");
        Server server = TimoCloudCore.getInstance().getServerManager().getServerByToken(targetToken);
        Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByToken(targetToken);
        String baseName = (String) json.get("base");
        String cordName = (String) json.get("cord");
        Communicatable target = null;
        if (server != null) target = server;
        else if (proxy != null) target = proxy;
        else if (baseName != null) target = TimoCloudCore.getInstance().getServerManager().getBase(baseName);
        else if (cordName != null) target = TimoCloudCore.getInstance().getServerManager().getCord(cordName);
        if (target == null) target = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) { // Handshakes
            case "SERVER_HANDSHAKE": {
                if (server == null) {
                    channel.close();
                    return;
                }
                if (!server.getToken().equals(data)) {
                    channel.close();
                    return;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, server);
                server.onConnect(channel);
                return;
            }
            case "PROXY_HANDSHAKE": {
                if (proxy == null) {
                    channel.close();
                    return;
                }
                if (!proxy.getToken().equals(data)) {
                    channel.close();
                    return;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, proxy);
                proxy.onConnect(channel);
                return;
            }
            case "BASE_HANDSHAKE": {
                InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
                if (!((List<String>) TimoCloudCore.getInstance().getFileManager().getConfig().get("allowedIPs")).contains(address.getHostAddress())) {
                    TimoCloudCore.getInstance().severe("Unknown base connected from " + address.getHostAddress() + ". If you want to allow this connection, please add the IP address to 'allowedIPs' in your config.yml, else, please block the port " + ((Integer) TimoCloudCore.getInstance().getFileManager().getConfig().get("socket-port")) + " in your firewall.");
                    channel.close();
                    return;
                }
                Base base = TimoCloudCore.getInstance().getServerManager().getBase(baseName, address, channel);
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, base);
                base.onConnect(channel);
                return;
            }
            case "CORD_HANDSHAKE": {
                InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
                if (!((List<String>) TimoCloudCore.getInstance().getFileManager().getConfig().get("allowedIPs")).contains(address.getHostAddress())) {
                    TimoCloudCore.getInstance().severe("Unknown cord connected from " + address.getHostAddress() + ". If you want to allow this connection, please add the IP address to 'allowedIPs' in your config.yml, else, please block the port " + ((Integer) TimoCloudCore.getInstance().getFileManager().getConfig().get("socket-port")) + " in your firewall.");
                    channel.close();
                    return;
                }
                Cord cord = TimoCloudCore.getInstance().getServerManager().getCord(cordName, address, channel);
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, cord);
                cord.onConnect(channel);
                return;
            }
        }

        // No Handshake, so we have to check if the channel is registered
        if (sender == null) {
            channel.close();
            TimoCloudCore.getInstance().severe("Unknown connection from " + channel.remoteAddress() + ", blocking. Please make sure to block the TimoCloudCore socket port (" + TimoCloudCore.getInstance().getSocketPort() + ") in your firewall to avoid this.");
            return;
        }

        switch (type) {
            case "GET_API_DATA": {
                JSONArray serverGroups = new JSONArray();
                JSONArray proxyGroups = new JSONArray();
                ObjectMapper objectMapper = ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper();
                try {
                    for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalInstance().getServerGroups())
                        serverGroups.add(objectMapper.writeValueAsString(serverGroupObject));
                    for (ProxyGroupObject proxyGroupObject : TimoCloudAPI.getUniversalInstance().getProxyGroups())
                        proxyGroups.add(objectMapper.writeValueAsString(proxyGroupObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String, Object> map = new HashMap<>();
                map.put("serverGroups", serverGroups);
                map.put("proxyGroups", proxyGroups);
                JSONObject jsonObject = new JSONObject(map);
                TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, "API_DATA", jsonObject);
                break;
            }
            case "FIRE_EVENT": {
                try {
                    TimoCloudCore.getInstance().getEventManager().fireEvent(
                            ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper().readValue(
                                    (String) data, EventUtil.getClassByEventType(
                                            EnumUtil.valueOf(EventType.class, (String) json.get("eventType")))));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while firing event: ");
                    e.printStackTrace();
                }
                break;
            }
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
                        msg.put("type", "TRANSFER");
                        msg.put("transferType", "SERVER_TEMPLATE");
                        msg.put("template", template);
                        msg.put("file", content);
                        channel.writeAndFlush(new JSONObject(msg).toString());
                        doAfterAmount.addOne();
                    }
                    if (globalDifferences != null) {
                        List<File> templateFiles = new ArrayList<>();
                        File templateDirectory = TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory();
                        for (String fileName : globalDifferences)
                            templateFiles.add(new File(templateDirectory, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, output);
                        String content = fileToString(output);
                        output.delete();
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("type", "TRANSFER");
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

    private String fileToString(File file) throws Exception {
        byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}
