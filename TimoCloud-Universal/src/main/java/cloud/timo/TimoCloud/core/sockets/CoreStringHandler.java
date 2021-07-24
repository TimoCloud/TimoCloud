package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.CordObject;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.utils.DoAfterAmount;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ChannelHandler.Sharable
public class CoreStringHandler extends BasicStringHandler {

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        Communicatable sender = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String targetId = message.getTarget();
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(targetId);
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByIdentifier(targetId);
        String baseName = (String) message.get("base");
        String cordName = (String) message.get("cord");
        Communicatable target = null;
        if (server != null) target = server;
        else if (proxy != null) target = proxy;
        else if (baseName != null) target = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseName);
        else if (cordName != null) target = TimoCloudCore.getInstance().getInstanceManager().getCord(cordName);
        if (target == null) target = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        MessageType type = message.getType();
        Object data = message.getData();
        InetAddress address = channel == null ? null : ((InetSocketAddress) channel.remoteAddress()).getAddress();
        switch (type) { // Handshakes
            case SERVER_HANDSHAKE: {
                if (server == null) {
                    closeChannel(channel);
                    return;
                }
                if (! (address.equals(server.getBase().getAddress()) || address.equals(server.getBase().getPublicAddress()))) {
                    TimoCloudCore.getInstance().severe("Server connected with different InetAddress than its base. Refusing connection.");
                    return;
                }
                if (! channel.attr(CoreRSAHandshakeHandler.RSA_KEY_ATTRIBUTE_KEY).get().equals(server.getPublicKey())) {
                    TimoCloudCore.getInstance().severe(String.format("Server %s connected with wrong public key. Please report this.", server.getName()));
                    return;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, server);
                server.onConnect(channel);
                server.onHandshakeSuccess();
                channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
                return;
            }
            case PROXY_HANDSHAKE: {
                if (proxy == null) {
                    closeChannel(channel);
                    return;
                }
                if (! (address.equals(proxy.getBase().getAddress()) || address.equals(proxy.getBase().getPublicAddress()))) {
                    TimoCloudCore.getInstance().severe("Proxy connected with different InetAddress than its base. Refusing connection.");
                    return;
                }
                if (! channel.attr(CoreRSAHandshakeHandler.RSA_KEY_ATTRIBUTE_KEY).get().equals(proxy.getPublicKey())) {
                    TimoCloudCore.getInstance().severe(String.format("Proxy %s connected with wrong public key. Please report this.", proxy.getName()));
                    return;
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, proxy);
                proxy.onConnect(channel);
                proxy.onHandshakeSuccess();
                channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
                return;
            }
            case BASE_HANDSHAKE: {
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
                PublicKey publicKey = channel.attr(CoreRSAHandshakeHandler.RSA_KEY_ATTRIBUTE_KEY).get();

                if (! TimoCloudCore.getInstance().getCorePublicKeyManager().redeemBaseKeyIfPermitted(publicKey)) {
                    channel.close();
                    return;
                }

                Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByPublicKey(publicKey);
                if (base == null) { // First connection
                    base = TimoCloudCore.getInstance().getInstanceManager().createBase(publicKey);
                }
                String publicIpConfig = base.getPublicIpConfig();
                if(!publicIpConfig.equalsIgnoreCase("AUTO")) {
                    try {
                        publicAddress = InetAddress.getByName(publicIpConfig);
                    } catch (Exception e) {
                        TimoCloudCore.getInstance().severe("Unable to resolve public ip address from bases.yml '" + publicIpConfig + "' for base " + baseName + ". Please make sure the base's hostname is configured correctly in your bases.yml.");
                    }
                }
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, base);
                base.onConnect(channel, address, publicAddress);
                base.onHandshakeSuccess();
                channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
                return;
            }
            case CORD_HANDSHAKE: {
                if (TimoCloudCore.getInstance().getInstanceManager().isCordConnected(cordName)) {
                    TimoCloudCore.getInstance().severe("Error while cord handshake: A cord with the name '" + cordName + "' is already conencted.");
                    return;
                }
                Cord cord = TimoCloudCore.getInstance().getInstanceManager().getOrCreateCord(cordName, address, channel);
                TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, cord);
                cord.onConnect(channel);
                cord.onHandshakeSuccess();
                channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
                return;
            }
        }

        // No Handshake, so we have to check if the channel is registered
        if (sender == null && channel != null) { // If channel is null, the message is internal (sender is core)
            closeChannel(channel);
            TimoCloudCore.getInstance().severe("Unknown connection from " + channel.remoteAddress() + ", blocking. Please make sure to block the TimoCloudCore socket port (" + TimoCloudCore.getInstance().getSocketPort() + ") in your firewall to avoid this.");
            return;
        }

        switch (type) {
            case GET_API_DATA: {
                Set<String> serverGroups = new HashSet<>();
                Set<String> proxyGroups = new HashSet<>();
                Set<String> servers = new HashSet<>();
                Set<String> proxies = new HashSet<>();
                Set<String> bases = new HashSet<>();
                Set<String> players = new HashSet<>();
                Set<String> cords = new HashSet<>();
                ObjectMapper objectMapper = ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper();
                try {
                    for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalAPI().getServerGroups()) {
                        serverGroups.add(objectMapper.writeValueAsString(serverGroupObject));
                    }
                    for (ProxyGroupObject proxyGroupObject : TimoCloudAPI.getUniversalAPI().getProxyGroups()) {
                        proxyGroups.add(objectMapper.writeValueAsString(proxyGroupObject));
                    }
                    for (ServerObject serverObject : TimoCloudAPI.getUniversalAPI().getServers()) {
                        servers.add(objectMapper.writeValueAsString(serverObject));
                    }
                    for (ProxyObject proxyObject : TimoCloudAPI.getUniversalAPI().getProxies()) {
                        proxies.add(objectMapper.writeValueAsString(proxyObject));
                    }
                    for (PlayerObject playerObject : TimoCloudAPI.getUniversalAPI().getPlayers()) {
                        players.add(objectMapper.writeValueAsString(playerObject));
                    }
                    for (BaseObject baseObject : TimoCloudAPI.getUniversalAPI().getBases()) {
                        bases.add(objectMapper.writeValueAsString(baseObject));
                    }
                    for (CordObject cordObject : TimoCloudAPI.getUniversalAPI().getCords()) {
                        cords.add(objectMapper.writeValueAsString(cordObject));
                    }
                    TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create()
                            .setType(MessageType.API_DATA)
                            .setData(
                                    Message.create()
                                            .set("serverGroups", serverGroups)
                                            .set("proxyGroups", proxyGroups)
                                            .set("servers", servers)
                                            .set("proxies", proxies)
                                            .set("players", players)
                                            .set("bases", bases)
                                            .set("cords", cords)
                            ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case FIRE_EVENT: {
                try {
                    TimoCloudCore.getInstance().getEventManager().fireEvent(
                            ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue(
                                    (String) data, EventUtil.getClassByEventType(
                                            EnumUtil.valueOf(EventType.class, (String) message.get("eT")))));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while firing event: ");
                    e.printStackTrace();
                }
                break;
            }
            case CORE_PARSE_COMMAND: {
                TimoCloudCore.getInstance().getCommandManager().onCommand((String) data, new CommandSender() {
                    @Override
                    public void sendMessage(String msg) {
                        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create()
                                .setType(MessageType.CORE_SEND_MESSAGE_TO_COMMAND_SENDER)
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
            case BASE_CHECK_IF_DELETABLE: {
                if (target == null || target instanceof Base) {
                    TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create().setType(MessageType.BASE_DELETE_DIRECTORY).setData(data));
                }
                break;
            }
            case SEND_PLUGIN_MESSAGE: {
                AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) data);
                TimoCloudCore.getInstance().getPluginMessageManager().onMessage(addressedPluginMessage);
                break;
            }
            case BASE_SERVER_TEMPLATE_REQUEST: {
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
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(
                                templateDifferences.stream().map(fileName -> new File(templateDirectory, fileName)).collect(Collectors.toList()),
                                templateDirectory,
                                outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType(MessageType.TRANSFER_TEMPLATE)
                                .set("transferType", "SERVER_TEMPLATE")
                                .set("template", template)
                                .set("file", content)
                                .setTarget(targetId)
                                .toString());
                    }
                    if (mapDifferences != null) {
                        File mapDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), server.getGroup().getName() + "_" + map);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(
                                mapDifferences.stream().map(fileName -> new File(mapDirectory, fileName)).collect(Collectors.toList()),
                                mapDirectory,
                                outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType(MessageType.TRANSFER_TEMPLATE)
                                .set("transferType", "SERVER_TEMPLATE")
                                .set("template", server.getGroup().getName() + "_" + map)
                                .set("file", content)
                                .setTarget(targetId)
                                .toString());
                    }
                    if (globalDifferences != null) {
                        File templateDirectory = TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(
                                globalDifferences.stream().map(fileName -> new File(templateDirectory, fileName)).collect(Collectors.toList()),
                                templateDirectory,
                                outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType(MessageType.TRANSFER_TEMPLATE)
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
            case BASE_PROXY_TEMPLATE_REQUEST: {
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
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(
                                templateDifferences.stream().map(fileName -> new File(templateDirectory, fileName)).collect(Collectors.toList()),
                                templateDirectory,
                                outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType(MessageType.TRANSFER_TEMPLATE)
                                .set("transferType", "PROXY_TEMPLATE")
                                .set("template", template)
                                .set("file", content)
                                .setTarget(targetId)
                                .toString());
                    }
                    if (globalDifferences != null) {
                        File templateDirectory = TimoCloudCore.getInstance().getFileManager().getProxyGlobalDirectory();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        TimoCloudCore.getInstance().getTemplateManager().zipFiles(
                                globalDifferences.stream().map(fileName -> new File(templateDirectory, fileName)).collect(Collectors.toList()),
                                templateDirectory,
                                outputStream);
                        String content = byteArrayToString(outputStream.toByteArray());
                        channel.writeAndFlush(Message.create()
                                .setType(MessageType.TRANSFER_TEMPLATE)
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
            case SERVER_LOG_ENTRY: {
                if (target instanceof Server) {
                    target.onMessage(message, sender);
                }
                break;
            }
            case PROXY_LOG_ENTRY: {
                if (target instanceof Proxy) {
                    target.onMessage(message, sender);
                }
                break;
            }
            default:
                target.onMessage(message, sender);
        }
    }

    private String byteArrayToString(byte[] bytes) throws Exception {
        return Base64.getEncoder().encodeToString(bytes);
    }

}
