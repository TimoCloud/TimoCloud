package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.sockets.handler.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class BaseStringHandler extends BasicStringHandler {

    @Override
    public void registerHandlers() {
        addHandler(new BaseDeleteDirectoryHandler());
        addHandler(new BaseHandshakeSuccessHandler());
        addHandler(new BaseInstanceKillHandler());
        addHandler(new BasePIDExistRequest());
        addHandler(new BaseProxyStoppedHandler());
        addHandler(new BaseServerStoppedHandler());
        addHandler(new BaseStartProxyHandler());
        addHandler(new BaseStartServerHandler());
        addHandler(new TransferTemplateHandler());
        addHandler(new BaseDownloadFileHandler());
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBase.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }

        if (!handleMessageInternal(message, originalMessage, channel)) {
            TimoCloudBase.getInstance().severe("Could not categorize json message: " + originalMessage);
        }
    }



}
