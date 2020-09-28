package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.base.sockets.handlers.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

@ChannelHandler.Sharable
public class BaseStringHandler extends BasicStringHandler {

    public BaseStringHandler() {
        addBasicHandlers();
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        MessageType type = message.getType();
        
        getMessageHandlers(type).forEach(messageHandler -> messageHandler.execute(message, channel));
    }

    private void addBasicHandlers() {
        addHandler(new BaseTransferTemplateHandler());
        addHandler(new BaseStartProxyHandler());
        addHandler(new BaseStartProxyHandler());
        addHandler(new BaseProxyStoppedHandler());
        addHandler(new BaseServerStoppedHandler());
        addHandler(new BaseHandshakeSuccessHandler());
        addHandler(new BaseDeleteDirectoryHandler());
    }

}
