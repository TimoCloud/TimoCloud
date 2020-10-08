package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.sockets.handlers.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.sockets.MessageTypeNotFoundExcpetion;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class BaseStringHandler extends BasicStringHandler {

    public BaseStringHandler() {
        addBasicHandlers();
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        MessageType type = message.getType();
        try {
            getMessageHandlers(type).forEach(messageHandler -> {
                try {
                    messageHandler.execute(message, channel);
                } catch (Exception e) {
                    TimoCloudBase.getInstance().severe("Messagehandler " + messageHandler.getClass().getSimpleName() + " threw an exception: ");
                    TimoCloudBase.getInstance().severe(e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (MessageTypeNotFoundExcpetion messageTypeNotFoundExcpetion) {
            TimoCloudBase.getInstance().severe(messageTypeNotFoundExcpetion.getMessage());
        }
    }

    private void addBasicHandlers() {
        addHandler(new BaseTransferTemplateHandler());
        addHandler(new BaseStartProxyHandler());
        addHandler(new BaseStartServerHandler());
        addHandler(new BaseProxyStoppedHandler());
        addHandler(new BaseServerStoppedHandler());
        addHandler(new BaseHandshakeSuccessHandler());
        addHandler(new BaseDeleteDirectoryHandler());
    }

}
