package cloud.timo.TimoCloud.api.internal;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;

import java.util.HashMap;
import java.util.Map;

public class APIRequestFutureStorage {

    private Map<String, APIRequestFuture> futures;

    public APIRequestFutureStorage() {
        this.futures = new HashMap<>();
    }

    public void addFuture(String id, APIRequestFuture future) {
        futures.put(id, future);
    }


    public void removeFuture(String futureId) {
        futures.remove(futureId);
    }

    public APIRequestFuture getFuture(String id) {
        return futures.get(id);
    }

    /**
     * Returns an APIRequestFuture and deletes it from the storage
     */
    public APIRequestFuture pollFuture(String id) {
        APIRequestFuture future = getFuture(id);
        removeFuture(id);
        return future;
    }
}
