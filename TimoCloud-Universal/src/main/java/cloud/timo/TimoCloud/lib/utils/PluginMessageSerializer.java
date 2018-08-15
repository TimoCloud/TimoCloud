package cloud.timo.TimoCloud.lib.utils;

import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.lib.json.JsonConverter;

import java.util.Map;

public class PluginMessageSerializer {

    public static AddressedPluginMessage deserialize(Map map) {
        return JsonConverter.convertMapToObject(map, AddressedPluginMessage.class);
    }

}
