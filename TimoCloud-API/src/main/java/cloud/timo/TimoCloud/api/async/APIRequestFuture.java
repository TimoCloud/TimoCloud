package cloud.timo.TimoCloud.api.async;

import java.util.Collection;
import java.util.LinkedHashSet;

public class APIRequestFuture {

    private APIRequest request;
    private Collection<APIRequestFutureListener> listeners = new LinkedHashSet<>();
    private APIResponse response;

    public APIRequestFuture(APIRequest request) {
        this.request = request;
    }

    public APIRequestFuture addListener(APIRequestFutureListener listener) {
        listeners.add(listener);
        if (response != null) {
            listener.requestComplete(response);
        }
        return this;
    }

    public APIRequestFuture addListeners(APIRequestFutureListener... listeners) {
        for (APIRequestFutureListener listener : listeners) {
            addListener(listener);
        }
        return this;
    }

    public APIRequestFuture removeListener(APIRequestFutureListener listener) {
        listeners.remove(this);
        return this;
    }

    public APIRequestFuture removeListeners(APIRequestFutureListener... listeners) {
        for (APIRequestFutureListener listener : listeners) {
            removeListener(listener);
        }
        return this;
    }

    protected void requestComplete(APIResponse response) {
        this.response = response;
        for (APIRequestFutureListener listener : getListeners()) {
            listener.requestComplete(response);
        }
    }

    public APIRequest getRequest() {
        return request;
    }

    /**
     * This will be null until we received a response
     */
    public APIResponse getResponse() {
        return response;
    }

    public Collection<APIRequestFutureListener> getListeners() {
        return listeners;
    }
}
