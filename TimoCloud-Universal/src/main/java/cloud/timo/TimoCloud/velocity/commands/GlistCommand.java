package cloud.timo.TimoCloud.velocity.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

import java.util.stream.Collectors;

public class GlistCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        for (ServerGroupObject serverGroupObject : TimoCloudAPI.getBungeeAPI().getThisProxy().getGroup().getServerGroups()) {
            for (ServerObject serverObject : serverGroupObject.getServers()) {
                invocation.source().sendMessage(
                        Component.text(ChatColorUtil.translateAlternateColorCodes('&', "&a[" + serverObject.getName() + "] &e(" + serverObject.getOnlinePlayerCount() + "): &r" +
                                serverObject.getOnlinePlayers().stream().map(PlayerObject::getName).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", ")))));
            }
        }
      
        invocation.source().sendMessage(Component.text(ChatColorUtil.translateAlternateColorCodes('&', "Total players online: " + TimoCloudAPI.getUniversalAPI().getProxyGroups().stream().mapToInt(ProxyGroupObject::getOnlinePlayerCount).sum())));
    }
}