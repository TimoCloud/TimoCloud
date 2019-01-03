package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.lib.protocol.Message;
import cloud.timo.TimoCloud.lib.protocol.MessageType;

public class PluginMessageManager {

    public void onMessage(AddressedPluginMessage message) {
        Communicatable communicatable = null;
        switch (message.getRecipient().getType()) {
            case CORE: // Message is addressed to us, call the event
                ((TimoCloudMessageAPIBasicImplementation) TimoCloudAPI.getMessageAPI()).onMessage(message);
                return;
            case SERVER:
                communicatable = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(message.getRecipient().getName());
                break;
            case PROXY:
                communicatable = TimoCloudCore.getInstance().getInstanceManager().getProxyByIdentifier(message.getRecipient().getName());
                break;
            case CORD:
                communicatable = TimoCloudCore.getInstance().getInstanceManager().getCord(message.getRecipient().getName());
                break;
        }
        if (communicatable == null) {
            TimoCloudCore.getInstance().severe("Unknown plugin message recipient: " + message.getRecipient());
            return;
        }
        communicatable.sendMessage(Message.create()
                .setType(MessageType.ON_PLUGIN_MESSAGE)
                .setData(message));
    }
}
