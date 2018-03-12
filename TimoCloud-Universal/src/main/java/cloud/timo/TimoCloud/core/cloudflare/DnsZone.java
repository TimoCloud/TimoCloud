package cloud.timo.TimoCloud.core.cloudflare;

import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.json.simple.JSONObject;

@AllArgsConstructor
@EqualsAndHashCode
public class DnsZone {
    @Getter
    private String id;
    @Getter
    private String name;

    public JSONObject toJson() {
        return JSONBuilder.create()
                .set("id", getId())
                .set("name", getName())
                .toJson();
    }

    public static DnsZone fromJson(JSONObject jsonObject) {
        return new DnsZone(
                (String) jsonObject.get("id"),
                (String) jsonObject.get("name")
        );
    }
}
