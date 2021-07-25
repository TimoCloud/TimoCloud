package cloud.timo.TimoCloud.api.implementations.managers;

import cloud.timo.TimoCloud.api.implementations.async.APIRequestFutureImplementation;
import cloud.timo.TimoCloud.api.implementations.async.APIResponse;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

public class APIResponseManager implements MessageListener {

    @Override
    public void onPluginMessage(AddressedPluginMessage addressedPluginMessage) {
        PluginMessage message = addressedPluginMessage.getMessage();
        if (!"TIMOCLOUD_API_RESPONSE".equals(message.getType())) {
            return;
        }

        APIResponse<?> apiResponse = APIResponse.fromPluginMessage(message);

        ((APIRequestFutureImplementation) TimoCloudInternalAPI.getApiRequestStorage().pollFuture(apiResponse.getId())).requestComplete(apiResponse);
    }

}
