package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.common.utils.DoAfterAmount;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Server;
import io.netty.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerTemplateRequestHandler extends MessageHandler {
    public ServerTemplateRequestHandler() {
        super(MessageType.BASE_SERVER_TEMPLATE_REQUEST);
    }

    @Override
    public void execute(Message message, Channel channel) {
        String targetId = message.getTarget();
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(targetId);

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
    }


    private String byteArrayToString(byte[] bytes) throws Exception {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
