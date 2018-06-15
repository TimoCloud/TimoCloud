package cloud.timo.TimoCloud.lib.utils;

import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.lib.messages.Message;

import java.util.Map;

public class PluginMessageSerializer {

    public static Message serialize(AddressedPluginMessage message) {
        return Message.create()
                        .set("sender", message.getSender().toString())
                        .set("recipient", message.getRecipient().toString())
                        .setData(Message.create()
                                .setType(message.getMessage().getType())
                                .setData(message.getMessage().getData()));
    }

    public static AddressedPluginMessage deserialize(Map map) {
        Message json = Message.create(map);
        Map messageObject = json.get("data", Map.class);
        try {
            return new AddressedPluginMessage(
                    MessageClientAddress.fromString((String) json.get("sender")),
                    MessageClientAddress.fromString((String) json.get("recipient")),
                    new PluginMessage(
                            (String) messageObject.get("type"),
                            (Map<String, Object>) messageObject.get("data")
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
