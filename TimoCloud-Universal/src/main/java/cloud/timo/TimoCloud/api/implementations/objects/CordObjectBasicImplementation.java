package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.api.objects.CordObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@NoArgsConstructor
public class CordObjectBasicImplementation implements CordObject {

    private String name;
    private InetSocketAddress address;
    private boolean connected;

    public CordObjectBasicImplementation(String name, InetSocketAddress address, boolean connected) {
        this.name = name;
        this.address = address;
        this.connected = connected;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return address;
    }

    @Override
    public InetAddress getIpAddress() {
        return getSocketAddress().getAddress();
    }

    @Override
    public int getPort() {
        return getSocketAddress().getPort();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void sendPluginMessage(PluginMessage message) {
        TimoCloudAPI.getMessageAPI().sendMessageToCord(message, getName());
    }
}
