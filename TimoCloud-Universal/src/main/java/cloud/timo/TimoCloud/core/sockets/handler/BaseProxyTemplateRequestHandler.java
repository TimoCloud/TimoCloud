package cloud.timo.TimoCloud.core.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.protocol.TransferType;
import cloud.timo.TimoCloud.common.utils.DoAfterAmount;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseProxyTemplateRequestHandler extends CoreMessageHandler{
    public BaseProxyTemplateRequestHandler() {
        super(MessageType.BASE_PROXY_TEMPLATE_REQUEST);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        if(!(target instanceof Proxy)) {
            return;
        }

        Proxy proxy = (Proxy) target;
        String targetId = message.getTarget();

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
                        .set("transferType", TransferType.PROXY_TEMPLATE)
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
                        .set("transferType",TransferType.PROXY_GLOBAL_TEMPLATE)
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
