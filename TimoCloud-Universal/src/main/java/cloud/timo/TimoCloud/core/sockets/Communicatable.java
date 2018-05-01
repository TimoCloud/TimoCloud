package cloud.timo.TimoCloud.core.sockets;

import io.netty.channel.Channel;
import org.json.simple.JSONObject;

/**
 * 'Communicatable' stands for every application in a TimoCloud network the core can communicate with via sockets (Server, Proxy, Base, Cord)
 */
public interface Communicatable {
    void onConnect(Channel channel);
    void onDisconnect();
    Channel getChannel();
    void onMessage(JSONObject message);
    void sendMessage(JSONObject message);
    void onHandshakeSuccess();
}
