package cloud.timo.TimoCloud.api.implementations.async;

import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestError;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.common.json.JsonConverter;
import lombok.Getter;
import lombok.Setter;

public class APIResponse<T> {

    @Getter
    private final String id;
    @Getter
    private final boolean success;
    @Getter
    private APIRequestError error;
    @Getter
    @Setter
    private T data;

    public APIResponse(APIRequest<?> request, T data) {
        this.id = request.getId();
        this.data = data;
        this.success = true;
    }

    public APIResponse(APIRequest<?> request, APIRequestError error) {
        this.id = request.getId();
        this.error = error;
        this.success = false;
    }

    public PluginMessage toPluginMessage() {
        return new PluginMessage("TIMOCLOUD_API_RESPONSE")
                .set("data", this);
    }

    public static APIResponse<?> fromPluginMessage(PluginMessage pluginMessage) {
        return JsonConverter.convertMapIfNecessary(pluginMessage.get("data"), APIResponse.class);
    }
}
