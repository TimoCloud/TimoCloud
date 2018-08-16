package cloud.timo.TimoCloud.api.implementations.async;

import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestError;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.lib.json.JsonConverter;
import com.google.gson.Gson;

public class APIResponse<T> {

    private static final Gson gson = new Gson();

    private String id;
    private boolean success;
    private APIRequestError error;
    private T data;

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
                .set("data", this);
    }

    public static APIResponse fromPluginMessage(PluginMessage pluginMessage) {
        return JsonConverter.convertMapIfNecessary(pluginMessage.get("data"), APIResponse.class);
    }
}
