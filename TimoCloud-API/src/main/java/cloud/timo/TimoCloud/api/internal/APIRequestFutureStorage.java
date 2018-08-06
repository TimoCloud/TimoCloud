package cloud.timo.TimoCloud.api.internal;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;

import java.util.HashMap;
import java.util.Map;

public class APIRequestFutureStorage {

    private Map<String, APIRequestFuture> futures;

    public APIRequestFutureStorage() {
        this.futures = new HashMap<>();
    }

    public void addRequest(String id, APIRequestFuture future) {
        futures.put(id, future);
    }


    public void removeRequest(String futureId) {
        futures.remove(futureId);
    }

    public void removeRequests(String ... futureIds) {
        for (String futureId : futureIds) {
            removeRequest(futureId);
        }
    }


    public APIRequestFuture getRequest(String id) {
        return futures.get(id);
    }

    /**
     * Returns an APIRequestFuture and deletes it from the storage
     */
    public APIRequestFuture pollRequest(String id) {
        APIRequestFuture future = getRequest(id);
        removeRequest(id);
        return future;
    }
}
