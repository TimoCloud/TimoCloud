package cloud.timo.TimoCloud.api.async;

import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

import java.util.HashMap;
import java.util.Map;

public class APIResponse {

    private String id;
    private boolean success;
    private APIRequestError error;
    private Map data;

    private APIResponse(PluginMessage pluginMessage) {
        this.id = pluginMessage.getString("id");
        this.success = pluginMessage.getBoolean("success");
        this.error = pluginMessage.containsProperty("error") ? (APIRequestError) pluginMessage.getObject("error") : null;
    }

    private APIResponse(APIRequest request, boolean success, APIRequestError error, Map data) {
        this.id = request.getId();
        this.success = success;
        this.error = error;
        this.data = data;
    }

    public APIResponse(APIRequest request, APIRequestError error, Map data) {
        this(request, false, error, data);
    }

    public APIResponse(APIRequest request, APIRequestError error) {
        this(request, error, new HashMap());
    }

    public APIResponse(APIRequest request) {
        this(request, true, null, new HashMap());
    }

    public String getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    public APIRequestError getError() {
        return error;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public PluginMessage toPluginMessage() {
        return new PluginMessage("TIMOCLOUD_API_RESPONSE")
                .set("id", getId())
                .set("success", isSuccess())
                .setIfNotNull("error", getError())
                .set("data", getData());
    }

    public static APIResponse fromPluginMessage(PluginMessage pluginMessage) {
        return new APIResponse(pluginMessage);
    }
}
