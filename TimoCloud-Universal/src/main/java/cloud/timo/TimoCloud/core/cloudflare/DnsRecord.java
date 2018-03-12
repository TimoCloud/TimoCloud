package cloud.timo.TimoCloud.core.cloudflare;


import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.json.simple.JSONObject;

@AllArgsConstructor
@EqualsAndHashCode
public class DnsRecord {

    @Getter
    private String id;
    @Getter
    private String type;
    @Getter
    private String name;
    @Getter
    private String content;
    @Getter
    private int ttl;
    @Getter
    private DnsZone zone;

    public DnsRecord() {
    }

    public JSONObject toJson() {
        return JSONBuilder.create()
                .setIfNotNull("id", getId())
                .setType(getType())
                .set("name", getName())
                .set("content", getContent())
                .set("ttl", getTtl())
                .set("zone_id", getZone().getId())
                .set("zone_name", getZone().getName())
                .toJson();
    }

    public static DnsRecord fromJson(JSONObject jsonObject) {
        return new DnsRecord(
                (String) jsonObject.get("id"),
                (String) jsonObject.get("type"),
                (String) jsonObject.get("name"),
                (String) jsonObject.get("content"),
                ((Number) jsonObject.get("ttl")).intValue(),
                new DnsZone(
                        (String) jsonObject.get("zone_id"),
                        (String) jsonObject.get("zone_name")
                )
        );
    }
}
