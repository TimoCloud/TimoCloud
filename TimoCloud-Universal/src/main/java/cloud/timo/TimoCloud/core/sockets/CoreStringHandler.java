package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.objects.CordObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.lib.messages.Message;
import cloud.timo.TimoCloud.lib.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.lib.utils.DoAfterAmount;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;
import cloud.timo.TimoCloud.lib.utils.PluginMessageSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class CoreStringHandler extends BasicStringHandler {

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        Communicatable sender = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String targetId = (String) message.get("target");
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerById(targetId);
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyById(targetId);
        String baseName = (String) message.get("base");
        String cordName = (String) message.get("cord");
        Communicatable target = null;
        if (server != null) target = server;
        else if (proxy != null) target = proxy;
        else if (baseName != null) target = TimoCloudCore.getInstance().getInstanceManager().getBase(baseName);
        else if (cordName != null) target = TimoCloudCore.getInstance().getInstanceManager().getCord(cordName);
        if (target == null) target = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String type = (String) message.get("type");
        Object data = message.get("data");
        InetAddress address = ((InetSocketAddress) channel.remoteAddress()).getAddress();
        switch (type) { // Handshakes
            case "SERVER_HANDSHAKE": {
                if (server == null) {
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
                if (proxy == null) {
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
                if (TimoCloudCore.getInstance().getInstanceManager().isBaseConnected(baseName)) {
                    TimoCloudCore.getInstance().severe("Error while base handshake: A base with the name '" + baseName + "' is already conencted.");
                    return;
                }
                InetAddress publicAddress = address;
                try {
                    publicAddress = InetAddress.getByName((String) message.get("publicAddress"));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Unable to resolve public ip address '" + message.get("publicAddress") + "' for base " + baseName + ". Please make sure the base's hostname is configured correctly in your operating system.");
                }
                Base base = TimoCloudCore.getInstance().getInstanceManager().getOrCreateBase(baseName, address, publicAddress, channel);
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
                if (TimoCloudCore.getInstance().getInstanceManager().isCordConnected(cordName)) {
                    TimoCloudCore.getInstance().severe("Error while cord handshake: A cord with the name '" + cordName + "' is already conencted.");
                    return;
                }
                Cord cord = TimoCloudCore.getInstance().getInstanceManager().getOrCreateCord(cordName, address, channel);
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
                List serverGroups = new ArrayList();
                List proxyGroups = new ArrayList();
                List cords = new ArrayList();
                ObjectMapper objectMapper = ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper();
                try {
                    for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalAPI().getServerGroups())
                        serverGroups.add(objectMapper.writeValueAsString(serverGroupObject));
                    for (ProxyGroupObject proxyGroupObject : TimoCloudAPI.getUniversalAPI().getProxyGroups())
                        proxyGroups.add(objectMapper.writeValueAsString(proxyGroupObject));
                    for (CordObject cordObject : TimoCloudAPI.getUniversalAPI().getCords())
                        cords.add(objectMapper.writeValueAsString(cordObject));
                    TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create()
                            .setType("API_DATA")
                            .setData(
                                    Message.create()
                                            .set("serverGroups", serverGroups)
                                            .set("proxyGroups", proxyGroups)
                                            .set("cords", cords)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "FIRE_EVENT": {
                try {
                    TimoCloudCore.getInstance().getEventManager().fireEvent(
                            ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue(
                                    (String) data, EventUtil.getClassByEventType(
                                            EnumUtil.valueOf(EventType.class, (String) message.get("eventType")))));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while firing event: ");
                    e.printStackTrace();
                }
                break;
            }
            case "PARSE_COMMAND": {
                TimoCloudCore.getInstance().getCommandManager().onCommand((String) data, new CommandSender() {
                    @Override
                    public void sendMessage(String msg) {
                        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create()
                                .setType("SEND_MESSAGE_TO_SENDER")
                                .set("sender", message.get("sender"))
                                .setData(msg));
                    }

                    @Override
                    public void sendError(String message) {
                        sendMessage("&c" + message);
                    }
                });
                break;
            }
            case "CHECK_IF_DELETABLE": {
                if (target == null || target instanceof Base) {
                    TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create().setType("DELETE_DIRECTORY").setData(data));
                }
                break;
            }
            case "PLUGIN_MESSAGE": {
                AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) data);
                TimoCloudCore.getInstance().getPluginMessageManager().onMessage(addressedPluginMessage);
                break;
            }
            case "SERVER_TEMPLATE_REQUEST": {
                server.getBase().setAvailableRam(server.getBase().getAvailableRam() + server.getGroup().getRam()); // Start paused, hence ram is free
                TimoCloudCore.getInstance().info("Base requested template update for server " + server.getName() + ". Sending update and starting server again...");
                Map differences = (Map) message.get("differences");
                List<String> templateDifferences = differences.containsKey("templateDifferences") ? (List<String>) differences.get("templateDifferences") : null;
                String template = message.containsKey("template") ? (String) message.get("template") : null;
                List<String> mapDifferences = differences.containsKey("mapDifferences") ? (List<String>) differences.get("mapDifferences") : null;
                String map = message.containsKey("map") ? (String) message.get("map") : null;
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
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType("TRANSFER")
                                .set("transferType", "SERVER_TEMPLATE")
                                .set("template", template)
                                .set("file", content)
                                .setTarget(targetId)
                                .toString());
                    }
                    if (mapDifferences != null) {
                        File mapDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), server.getGroup().getName() + "_" + map);
                        List<File> mapFiles = new ArrayList<>();
                        for (String fileName : mapDifferences) mapFiles.add(new File(mapDirectory, fileName));
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(mapFiles, mapDirectory, outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType("TRANSFER")
                                .set("transferType", "SERVER_TEMPLATE")
                                .set("template", server.getGroup().getName() + "_" + map)
                                .set("file", content)
                                .setTarget(targetId)
                                .toString());
                    }
                    if (globalDifferences != null) {
                        List<File> templateFiles = new ArrayList<>();
                        File templateDirectory = TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory();
                        for (String fileName : globalDifferences)
                            templateFiles.add(new File(templateDirectory, fileName));
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType("TRANSFER")
                                .set("transferType", "SERVER_GLOBAL_TEMPLATE")
                                .set("file", content)
                                .setTarget(targetId)
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
                Map differences = (Map) message.get("differences");
                List<String> templateDifferences = differences.containsKey("templateDifferences") ? (List<String>) differences.get("templateDifferences") : null;
                String template = message.containsKey("template") ? (String) message.get("template") : null;
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
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType("TRANSFER")
                                .set("transferType", "PROXY_TEMPLATE")
                                .set("template", template)
                                .set("file", content)
                                .setTarget(targetId)
                                .toString());
                    }
                    if (globalDifferences != null) {
                        List<File> templateFiles = new ArrayList<>();
                        File templateDirectory = TimoCloudCore.getInstance().getFileManager().getProxyGlobalDirectory();
                        for (String fileName : globalDifferences)
                            templateFiles.add(new File(templateDirectory, fileName));
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(templateFiles, templateDirectory, outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType("TRANSFER")
                                .set("transferType", "PROXY_GLOBAL_TEMPLATE")
                                .set("file", content)
                                .setTarget(targetId)
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
                target.onMessage(message);
        }
    }

    private String byteArrayToString(byte[] bytes) throws Exception {
        return Base64.getEncoder().encodeToString(bytes);
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
