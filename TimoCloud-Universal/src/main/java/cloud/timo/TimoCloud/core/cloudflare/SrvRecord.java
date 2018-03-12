package cloud.timo.TimoCloud.core.cloudflare;

import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import org.json.simple.JSONObject;

public class SrvRecord extends DnsRecord {

    private int priority;
    private int weight;
    private int port;
    private String target;

    public SrvRecord(String id, String type, String name, String content, int ttl, DnsZone zone, int priority, int weight, int port, String target) {
        super(id, type, name, content, ttl, zone);
        this.priority = priority;
        this.weight = weight;
        this.port = port;
        this.target = target;
    }

    public int getPriority() {
        return priority;
    }

    public int getWeight() {
        return weight;
    }

    public int getPort() {
        return port;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public JSONObject toJson() {
        return JSONBuilder.create()
                .setIfNotNull("id", getId())
                .setType(getType())
                .set("name", getName())
                .set("content", getContent())
                .set("ttl", getTtl())
                .set("zone_id", getZone().getId())
                .set("zone_name", getZone().getName())
                .setData(JSONBuilder.create()
                        .set("priority", getPriority())
                        .set("weight", getWeight())
                        .set("port", getPort())
                        .set("target", getTarget())
                        .set("service", "_minecraft")
                        .set("proto", "_tcp")
                        .set("name", getName())
                        .toJson())
                .toJson();
    }
}
