package cloud.timo.TimoCloud.base.objects;

import org.json.simple.JSONObject;

public class BaseProxyObject {
    private String name;
    private String group;
    private int ram;
    private boolean isStatic;
    private String token;
    private JSONObject templateHash;
    private JSONObject globalHash;

    public BaseProxyObject(String name, String group, int ram, boolean isStatic, String token, JSONObject templateHash, JSONObject globalHash) {
        this.name = name;
        this.group = group;
        this.ram = ram;
        this.isStatic = isStatic;
        this.token = token;
        this.templateHash = templateHash;
        this.globalHash = globalHash;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public int getRam() {
        return ram;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getToken() {
        return token;
    }

    public JSONObject getTemplateHash() {
        return templateHash;
    }

    public JSONObject getGlobalHash() {
        return globalHash;
    }
}
