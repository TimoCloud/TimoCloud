package cloud.timo.TimoCloud.base.sockets.handlers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;

public class BaseDeleteDirectoryHandler extends MessageHandler {
    public BaseDeleteDirectoryHandler() {
        super(MessageType.BASE_DELETE_DIRECTORY);
    }

    @Override
    public void execute(Message message, Channel channel) {
        File dir = new File((String) message.getData());
        if (dir.exists() && dir.isDirectory()) FileDeleteStrategy.FORCE.deleteQuietly(dir);    }
}
