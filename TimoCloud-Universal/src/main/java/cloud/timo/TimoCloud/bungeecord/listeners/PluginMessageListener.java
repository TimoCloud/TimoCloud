package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class PluginMessageListener implements MessageListener {

    @Override
    public void onPluginMessage(AddressedPluginMessage message) {
        PluginMessage pluginMessage = message.getMessage();

        if (pluginMessage.getType().equals("SubCommandRegister")) {
            TimoCloudBungee.getInstance().getTimoCloudCommand().addSubCommandName(pluginMessage.getString("Command"));
        }
        if (pluginMessage.getType().equals("SubCommandUnregister")) {
            TimoCloudBungee.getInstance().getTimoCloudCommand().removeSubCommandName(pluginMessage.getString("Command"));
        }
    }
}
