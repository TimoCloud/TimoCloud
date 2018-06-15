package cloud.timo.TimoCloud.core.cloudflare;

import cloud.timo.TimoCloud.lib.json.JsonObjectBuilder;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class DnsZone {
    @Getter
    private String id;
    @Getter
    private String name;

    public JsonObject toJson() {
        return JsonObjectBuilder.create()
                .set("id", getId())
                .set("name", getName())
                .toJsonObject();
    }

    public static DnsZone fromJson(JsonObject jsonObject) {
        return new DnsZone(
                jsonObject.get("id").getAsString(),
                jsonObject.get("name").getAsString()
        );
    }
}
