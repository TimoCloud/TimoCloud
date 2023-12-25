package cloud.timo.TimoCloud.base.sockets.handler;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.core.utils.paperapi.PaperAPI;
import io.netty.channel.Channel;

import java.io.File;

public class BaseDownloadFileHandler extends MessageHandler {

    public BaseDownloadFileHandler() {
        super(MessageType.BASE_DOWNLOAD_FILE);
    }

    @Override
    public void execute(Message message, Channel channel) {
        String groupName = (String) message.get("groupName");
        String url = (String) message.get("url");
        String fileName = (String) message.get("fileName");
        String storageName = (String) message.get("storageName");
        String groupType = (String) message.get("groupType");
        File templateDirectory = null;
        if (groupType.equals("server")) {
            templateDirectory = new File(TimoCloudBase.getInstance().getFileManager().getServerStaticDirectory(), groupName);
        } else if (groupType.equals("proxy")) {
            templateDirectory = new File(TimoCloudBase.getInstance().getFileManager().getProxyStaticDirectory(), groupName);
        }

        if (!templateDirectory.exists()) templateDirectory.mkdirs();
        File downloadLocation = new File(templateDirectory, storageName);
        PaperAPI.download(url, downloadLocation);
        TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create()
                .setType(MessageType.SERVER_DOWNLOAD_FILE_FINISHED)
                .setTarget(message.getTarget())
                .set("fileName", fileName)
        );

    }
}
