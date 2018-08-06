package cloud.timo.TimoCloud.api.async;

public interface APIRequestFutureListener {

    /**
     * Called when the API request is completed
     */
    void requestComplete(APIResponse response);

}
