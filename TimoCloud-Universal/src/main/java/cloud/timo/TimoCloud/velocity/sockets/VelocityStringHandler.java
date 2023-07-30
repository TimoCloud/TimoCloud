package cloud.timo.TimoCloud.velocity.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.sockets.handler.*;
import io.netty.channel.Channel;

public class VelocityStringHandler extends BasicStringHandler {

    @Override
    public void registerHandlers() {
        addHandler(new APIDataHandler());
        addHandler(new CordSetIPHandler());
        addHandler(new CoreSendMessageToCommandSenderHandler());
        addHandler(new EventFiredHandler());
        addHandler(new OnPluginMessageHandler());
        addHandler(new ProxyAddServerHandler());
        addHandler(new ProxyExecuteCommandHandler());
        addHandler(new ProxyHandshakeSuccessHandler());
        addHandler(new ProxyRemoveServerHandler());
        addHandler(new ProxySendMessageHandler());
        addHandler(new ProxySendPlayerHandler());
        addHandler(new ProxyStopHandler());
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudVelocity.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }
        if(!handleMessageInternal(message, originalMessage, channel)) {
            TimoCloudVelocity.getInstance().severe("Could not categorize json message: " + message);
        }
    }



}
