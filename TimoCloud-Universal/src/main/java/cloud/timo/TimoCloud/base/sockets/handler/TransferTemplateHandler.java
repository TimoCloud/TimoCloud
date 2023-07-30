package cloud.timo.TimoCloud.base.sockets.handler;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;

public class TransferTemplateHandler extends MessageHandler {

    public TransferTemplateHandler() {
        super(MessageType.TRANSFER_TEMPLATE);
    }

    @Override
    public void execute(Message message, Channel channel) {
        try {
            InputStream inputStream = new ByteArrayInputStream(stringToByteArray((String) message.get("file")));
            switch ((String) message.get("transferType")) {
                case "SERVER_TEMPLATE":
                    TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, new File(TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory(), (String) message.get("template")));
                    TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_TRANSFER_FINISHED).setTarget(message.getTarget()));
                    break;
                case "SERVER_GLOBAL_TEMPLATE":
                    TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory());
                    TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_TRANSFER_FINISHED).setTarget(message.getTarget()));
                    break;
                case "PROXY_TEMPLATE":
                    TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, new File(TimoCloudBase.getInstance().getFileManager().getProxyTemplatesDirectory(), (String) message.get("template")));
                    TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.PROXY_TRANSFER_FINISHED).setTarget(message.getTarget()));
                    break;
                case "PROXY_GLOBAL_TEMPLATE":
                    TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory());
                    TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.PROXY_TRANSFER_FINISHED).setTarget(message.getTarget()));
                    break;
            }
            TimoCloudBase.getInstance().getInstanceManager().setDownloadingTemplate(false);
        } catch (Exception e) {
            TimoCloudBase.getInstance().severe("Error while unpacking transferred files: ");
            TimoCloudBase.getInstance().severe(e);
        }
    }

    private byte[] stringToByteArray(String input) {
        return Base64.getDecoder().decode(input.getBytes());
    }
}
