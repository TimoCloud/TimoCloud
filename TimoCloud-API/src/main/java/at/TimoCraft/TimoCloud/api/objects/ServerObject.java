package at.TimoCraft.TimoCloud.api.objects;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by Timo on 02.09.17.
 */
public interface ServerObject extends Serializable {

    String getName();

    GroupObject getGroup();

    String getGroupName();

    String getState();

    void setState(String state);

    String getExtra();

    void setExtra(String extra);

    String getMap();

    String getMotd();

    int getCurrentPlayers();

    int getMaxPlayers();

    InetSocketAddress getSocketAddress();

    InetAddress getIpAddress();

    int getPort();

    boolean isSortedOut();

}
