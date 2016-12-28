package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Timo on 28.12.16.
 */

public class ServerClientSocket {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private TemporaryServer server;
    private List<String> pendingIncoming;

    public ServerClientSocket(Socket socket, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        pendingIncoming = new ArrayList<>();
    }

    public void receive() {
        while (true) {
            try {
                onMessage(in.readLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.append(message);
        out.flush();
    }

    public void onMessage(String message) {
        pendingIncoming.add(message);
        redirect();
    }

    public void redirect() {
        if (server == null) {
            return;
        }
        for (String message : pendingIncoming) {
            server.onSocketMessage(message);
        }
    }

    public TemporaryServer getServer() {
        return server;
    }

    public void setServer(TemporaryServer server) {
        this.server = server;
    }
}
