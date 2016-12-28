package at.TimoCraft.TimoCloud.bungeecord.managers;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.ServerClientSocket;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timo on 28.12.16.
 */
public class SocketManager {

    private ServerSocket serverSocket;
    private Map<ServerClientSocket, TemporaryServer> sockets;
    private int port;

    public SocketManager(int port) {
        this.port = port;
        sockets = new HashMap<>();
    }

    public void init() { //Async call!
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            try {
                handleSocket(serverSocket.accept());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSocket(Socket clientSocket) {
        try {
            TimoCloud.info("Socket connection accepted from " + clientSocket.getInetAddress());
            clientSocket.setKeepAlive(true);
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            ServerClientSocket serverClientSocket = new ServerClientSocket(clientSocket, out, in);
            sockets.put(serverClientSocket, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerServerClientSocket(ServerClientSocket socket, TemporaryServer server) {
        sockets.put(socket, server);
    }
}
