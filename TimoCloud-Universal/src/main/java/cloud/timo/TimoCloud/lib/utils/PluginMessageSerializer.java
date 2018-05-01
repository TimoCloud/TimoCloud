package cloud.timo.TimoCloud.lib.utils;

import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import org.json.simple.JSONObject;

import java.util.Map;

public class PluginMessageSerializer {

    public static String serialize(AddressedPluginMessage message) {
        return JSONBuilder.create()
                        .set("sender", message.getSender().toString())
                        .set("recipient", message.getRecipient().toString())
                        .setData(JSONBuilder.create()
                                .setType(message.getMessage().getType())
                                .setData(message.getMessage().getData())
                                .toJson())
                        .toString();
    }

    public static AddressedPluginMessage deserialize(String message) {
        JSONObject json = JSONBuilder.createFromJsonString(message).toJson();
        Map messageObject = (JSONObject) json.get("data");
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
