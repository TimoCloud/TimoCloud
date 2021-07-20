package cloud.timo.TimoCloud.velocity.listeners;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.text.TextComponent;

public class ProxyPing {

    @Subscribe(order = PostOrder.EARLY)
    public void onProxyPingEvent(ProxyPingEvent event) {
        ProxyObject proxyObject = TimoCloudAPI.getProxyAPI().getThisProxy();

        ServerPing serverPing = event.getPing();
        serverPing = serverPing.asBuilder().maximumPlayers(proxyObject.getGroup().getMaxPlayerCount()).onlinePlayers(
                proxyObject.getGroup().getOnlinePlayerCount()).build();
        if (TimoCloudVelocity.getInstance().getFileManager().getConfig().getBoolean("useGlobalMotd"))
            serverPing = serverPing.asBuilder().description(TextComponent.of(ChatColorUtil.translateAlternateColorCodes('&', proxyObject.getGroup().getMotd()))).build();
        event.setPing(serverPing);
    }

}
