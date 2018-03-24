package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.CordObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import cloud.timo.TimoCloud.lib.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.lib.utils.DoAfterAmount;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
        InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
        switch (type) { // Handshakes
            case "SERVER_HANDSHAKE": {
                if (server == null || !server.getToken().equals(data)) {
                    closeChannel(channel);
                    return;
                }
                if (!address.equals(server.getBase().getAddress())) {
                    TimoCloudCore.getInstance().severe("Server connected with different InetAddress than its base. Refusing connection.");
                    return;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, server);
                server.onConnect(channel);
                server.onHandshakeSuccess();
                return;
            }
            case "PROXY_HANDSHAKE": {
                if (proxy == null || !proxy.getToken().equals(data)) {
                    closeChannel(channel);
                    return;
                }
                if (!address.equals(proxy.getBase().getAddress())) {
                    TimoCloudCore.getInstance().severe("Proxy connected with different InetAddress than its base. Refusing connection.");
                    return;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, proxy);
                proxy.onConnect(channel);
                proxy.onHandshakeSuccess();
                return;
            }
            case "BASE_HANDSHAKE": {
                if (!ipAllowed(address)) {
                    TimoCloudCore.getInstance().severe("Unknown base connected from " + address.getHostAddress() + ". If you want to allow this connection, please add the IP address to 'allowedIPs' in your config.yml, else, please block the port " + ((Integer) TimoCloudCore.getInstance().getFileManager().getConfig().get("socket-port")) + " in your firewall.");
                    closeChannel(channel);
                    return;
                }
                if (TimoCloudCore.getInstance().getServerManager().isBaseConnected(baseName)) {
                    TimoCloudCore.getInstance().severe("Error while base handshake: A base with the name '" + baseName + "' is already conencted.");
                    return;
                }
                InetAddress publicAddress = address;
                try {
                    publicAddress = InetAddress.getByName((String) json.get("publicAddress"));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Unable to resolve public ip address '" + json.get("publicAddress") + "' for base " + baseName + ". Please make sure the base's hostname is configured correctly in your operating system.");
                }
                Base base = TimoCloudCore.getInstance().getServerManager().getOrCreateBase(baseName, address, publicAddress, channel);
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, base);
                base.onConnect(channel);
                base.onHandshakeSuccess();
                return;
            }
            case "CORD_HANDSHAKE": {
                if (!ipAllowed(address)) {
                    TimoCloudCore.getInstance().severe("Unknown cord connected from " + address.getHostAddress() + ". If you want to allow this connection, please add the IP address to 'allowedIPs' in your config.yml, else, please block the port " + ((Integer) TimoCloudCore.getInstance().getFileManager().getConfig().get("socket-port")) + " in your firewall.");
                    closeChannel(channel);
                    return;
                }
                if (TimoCloudCore.getInstance().getServerManager().isCordConnected(baseName)) {
                    TimoCloudCore.getInstance().severe("Error while cord handshake: A cord with the name '" + baseName + "' is already conencted.");
                    return;
                }
                Cord cord = TimoCloudCore.getInstance().getServerManager().getOrCreateCord(cordName, address, channel);
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, cord);
                cord.onConnect(channel);
                cord.onHandshakeSuccess();
                return;
            }
        }

        // No Handshake, so we have to check if the channel is registered
        if (sender == null) {
            closeChannel(channel);
            TimoCloudCore.getInstance().severe("Unknown connection from " + channel.remoteAddress() + ", blocking. Please make sure to block the TimoCloudCore socket port (" + TimoCloudCore.getInstance().getSocketPort() + ") in your firewall to avoid this.");
            return;
        }

        switch (type) {
            case "GET_API_DATA": {
                JSONArray serverGroups = new JSONArray();
                JSONArray proxyGroups = new JSONArray();
                JSONArray cords = new JSONArray();
                ObjectMapper objectMapper = ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper();
                try {
                    for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalInstance().getServerGroups())
                        serverGroups.add(objectMapper.writeValueAsString(serverGroupObject));
                    for (ProxyGroupObject proxyGroupObject : TimoCloudAPI.getUniversalInstance().getProxyGroups())
                        proxyGroups.add(objectMapper.writeValueAsString(proxyGroupObject));
                    for (CordObject cordObject : TimoCloudAPI.getUniversalInstance().getCords())
                        cords.add(objectMapper.writeValueAsString(cordObject));
                    TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, "API_DATA", JSONBuilder.create()
                            .set("serverGroups", serverGroups)
                            .set("proxyGroups", proxyGroups)
                            .set("cords", cords)
                            .toJson());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            case "PARSE_COMMAND": {
                TimoCloudCore.getInstance().getCommandManager().onCommand((str) -> {
                    TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, JSONBuilder.create()
                            .setType("SEND_MESSAGE_TO_SENDER")
                            .set("sender", json.get("sender"))
                            .setData(str)
                            .toJson());
                }, false, (String) data);
                break;
            }
            case "CHECK_IF_DELETABLE": {
                if (target == null || target instanceof Base) {
                    TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, "DELETE_DIRECTORY", data);
                }
                break;
            }
            case "SERVER_TEMPLATE_REQUEST": {
                server.getBase().setAvailableRam(server.getBase().getAvailableRam() + server.getGroup().getRam()); // Start paused, hence ram is free
                TimoCloudCore.getInstance().info("Base requested template update for server " + server.getName() + ". Sending update and starting server again...");
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
                server.setTemplateUpdate(doAfterAmount);
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
                        channel.writeAndFlush(JSONBuilder.create()
                                .setType("TRANSFER")
                                .set("transferType", "SERVER_TEMPLATE")
                                .set("template", template)
                                .set("file", content)
                                .set("target", targetToken)
                                .toString());
                    }
                    if (mapDifferences != null) {
                        File mapDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), server.getGroup().getName() + "_" + map);
                        List<File> mapFiles = new ArrayList<>();
                        for (String fileName : mapDifferences) mapFiles.add(new File(mapDirectory, fileName));
                        File output = new File(TimoCloudCore.getInstance().getFileManager().getTemporaryDirectory(), new Date().getTime() + "");
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(mapFiles, mapDirectory, output);
                        String content = fileToString(output);
                        output.delete();
                        channel.writeAndFlush(JSONBuilder.create()
                                .setType("TRANSFER")
                                .set("transferType", "SERVER_TEMPLATE")
                                .set("template", server.getGroup().getName() + "_" + map)
                                .set("file", content)
                                .set("target", targetToken)
                                .toString());
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
                        channel.writeAndFlush(JSONBuilder.create()
                                .setType("TRANSFER")
                                .set("transferType", "SERVER_GLOBAL_TEMPLATE")
                                .set("file", content)
                                .set("target", targetToken)
                                .toString());
                    }
                    doAfterAmount.setAmount(amount);
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while sending template files: ");
                    e.printStackTrace();
                }
                break;
            }
            case "PROXY_TEMPLATE_REQUEST": {
                proxy.getBase().setAvailableRam(proxy.getBase().getAvailableRam() + proxy.getGroup().getRam()); // Start paused, hence ram is free
                TimoCloudCore.getInstance().info("Base requested template update for proxy " + proxy.getName() + ". Sending update and starting server again...");
                JSONObject differences = (JSONObject) json.get("differences");
                List<String> templateDifferences = differences.containsKey("templateDifferences") ? (List<String>) differences.get("templateDifferences") : null;
                String template = json.containsKey("template") ? (String) json.get("template") : null;
                List<String> globalDifferences = differences.containsKey("globalDifferences") ? (List<String>) differences.get("globalDifferences") : null;
                int amount = 0;
                if (templateDifferences != null) amount++;
                if (globalDifferences != null) amount++;
                DoAfterAmount doAfterAmount = new DoAfterAmount(amount, proxy::start);
                proxy.setTemplateUpdate(doAfterAmount);
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
                        channel.writeAndFlush(JSONBuilder.create()
                                .setType("TRANSFER")
                                .set("transferType", "PROXY_TEMPLATE")
                                .set("template", template)
                                .set("file", content)
                                .set("target", targetToken)
                                .toString());
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
                        channel.writeAndFlush(JSONBuilder.create()
                                .setType("TRANSFER")
                                .set("transferType", "PROXY_GLOBAL_TEMPLATE")
                                .set("file", content)
                                .set("target", targetToken)
                                .toString());
                    }
                    doAfterAmount.setAmount(amount);
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

    private boolean ipAllowed(InetAddress inetAddress) {
        for (String ipString : (List<String>) TimoCloudCore.getInstance().getFileManager().getConfig().get("allowedIPs")) {
            try {
                for (InetAddress allowed : InetAddress.getAllByName(ipString)) {
                    if (inetAddress.equals(allowed)) return true;
                }
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe("Error while parsing InetAddress: ");
                e.printStackTrace();
            }
        }
        return false;
    }

}
