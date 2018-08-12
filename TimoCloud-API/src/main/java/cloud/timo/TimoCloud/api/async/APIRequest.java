package cloud.timo.TimoCloud.api.async;

import java.util.Map;

public interface APIRequest<T> {

    APIRequestFuture<T> submit();

    /**
     * @return A unique ID identifying the API request
     */
    String getId();

    /**
     * @return The (server-, proxy-, server group-, proxy group-id) this APIRequest targets
     */
    String getTarget();

    /**
     * @return The APIRequest's type
     */
    APIRequestType getType();

    /**
     * @return Parameters for the APIRequest. Parameters for APIRequest setter types are usually stored as "value"
     */
    Map getData();
}
