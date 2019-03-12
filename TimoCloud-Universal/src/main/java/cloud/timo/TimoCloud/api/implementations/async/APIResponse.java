package cloud.timo.TimoCloud.api.implementations.async;

import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestError;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.common.json.JsonConverter;

public class APIResponse<T> {

    private String id;
    private boolean success;
    private APIRequestError error;
    private T data;

    public APIResponse(APIRequest request, T data) {
        this.id = request.getId();
        this.data = data;
        this.success = true;
    }

    public APIResponse(APIRequest request, APIRequestError error) {
        this.id = request.getId();
        this.error = error;
        this.success = false;
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
                .set("data", this);
    }

    public static APIResponse fromPluginMessage(PluginMessage pluginMessage) {
        return JsonConverter.convertMapIfNecessary(pluginMessage.get("data"), APIResponse.class);
    }
}
