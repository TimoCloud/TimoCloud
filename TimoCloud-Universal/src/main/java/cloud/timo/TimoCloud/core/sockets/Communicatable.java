package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import io.netty.channel.Channel;

/**
 * 'Communicatable' stands for every application in a TimoCloud network the core can communicate with via sockets (Server, Proxy, Base, Cord)
 */
public interface Communicatable {

    void onConnect(Channel channel);

    void onDisconnect();

    boolean isConnected();

    Channel getChannel();

    void onMessage(Message message, Communicatable sender);

    void sendMessage(Message message);

    void onHandshakeSuccess();

}
