package cloud.timo.TimoCloud.api.implementations.async;

import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestError;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

public class APIResponse<T> {

    private String id;
    private boolean success;
    private APIRequestError error;
    private T data;

    private APIResponse(PluginMessage pluginMessage) {
        this.id = pluginMessage.getString("id");
        this.success = pluginMessage.getBoolean("success");
        this.error = pluginMessage.containsProperty("error") ? (APIRequestError) pluginMessage.getObject("error") : null;
    }

    private APIResponse(APIRequest request, boolean success, APIRequestError error, T data) {
        this.id = request.getId();
        this.success = success;
        this.error = error;
        this.data = data;
    }

    public APIResponse(APIRequest request, APIRequestError error, T data) {
        this(request, false, error, data);
    }

    public APIResponse(APIRequest request, APIRequestError error) {
        this(request, error, null);
    }

    public APIResponse(APIRequest request) {
        this(request, true, null, null);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
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
