package at.TimoCraft.TimoCloud.api.objects;

import java.net.InetSocketAddress;

/**
 * Created by Timo on 02.09.17.
 */
public class ServerObject {
    private String name;
    private GroupObject group;
    private int port;
    private String state = "STARTING";
    private String extra = "";
    private String motd = "";
    private String map = "";
    private int currentPlayers = 0;
    private int maxPlayers = 0;
}
