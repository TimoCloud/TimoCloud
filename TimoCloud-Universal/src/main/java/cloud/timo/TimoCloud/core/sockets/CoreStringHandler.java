package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.common.utils.DoAfterAmount;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.handler.*;
import cloud.timo.TimoCloud.core.sockets.handshake.BaseHandshakeHandler;
import cloud.timo.TimoCloud.core.sockets.handshake.CordHandshakeHandler;
import cloud.timo.TimoCloud.core.sockets.handshake.ProxyHandshakeHandler;
import cloud.timo.TimoCloud.core.sockets.handshake.ServerHandshakeHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@ChannelHandler.Sharable
public class CoreStringHandler extends BasicStringHandler {

    @Override
    public void registerHandlers() {
        //Handshakes
        addHandler(new BaseHandshakeHandler());
        addHandler(new CordHandshakeHandler());
        addHandler(new ProxyHandshakeHandler());
        addHandler(new ServerHandshakeHandler());

        //Defaults
        addHandler(new BaseCheckIfDeletableHandler());
        addHandler(new BaseProxyTemplateRequestHandler());
        addHandler(new BaseServerTemplateRequestHandler());
        addHandler(new CoreParseCommandHandler());
        addHandler(new GetAPIDataHandler());
        addHandler(new FireEventHandler());
        addHandler(new ProxyLogEntryHandler());
        addHandler(new SendPluginMessageHandler());
        addHandler(new ServerLogEntryHandler());
    }

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
        InetAddress address = channel == null ? null : ((InetSocketAddress) channel.remoteAddress()).getAddress();

        if(getHandlers().containsKey(message.getType())) {
            MessageHandler messageHandler = getHandlers().get(message.getType());

            if(messageHandler instanceof CoreMessageHandler) {
                ((CoreMessageHandler) messageHandler).execute(message, target, address, channel);
            } else {
                messageHandler.execute(message, channel);
            }
        } else {
            target.onMessage(message, sender);
        }

    }

}
