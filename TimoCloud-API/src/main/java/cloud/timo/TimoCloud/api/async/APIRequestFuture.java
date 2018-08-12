package cloud.timo.TimoCloud.api.async;

import java.util.Collection;

public interface APIRequestFuture<T> {

    /**
     * @param listener Will be called if the APIRequest was successful and a response arrived
     * @return The same APIRequestFuture this call is made on, for chaining statements
     */
    APIRequestFuture<T> onCompletion(APIRequestFutureListenerWithoutParams<T> listener);

    /**
     * @param listener Will be called if the APIRequest was successful and a response arrived
     * @return The same APIRequestFuture this call is made on, for chaining statements
     */
    APIRequestFuture<T> onCompletion(APIRequestFutureListenerWithParams<T> listener);

    /**
     * @param handler Will be called when an error is returned because of an invalid request
     * @return The same APIRequestFuture this call is made on, for chaining statements
     */
    APIRequestFuture<T> onError(APIRequestErrorHandler handler);

    /**
     * @return Whether the APIRequest has been completed or not
     */
    boolean isCompleted();

    /**
     * @return Whether the APIRequest was successful or not. If the request is not completed yet, this will return null.
     */
    Boolean isSuccess();

    /**
     * @return The APIRequest this future belongs to
     */
    APIRequest<T> getAPIRequest();

    /**
     * @return If no response has arrived yet or an error has occurred, this will return null
     */
    T getResponse();

    /**
     * @return If an error occurred while processing the APIRequest, this method will return it - otherwise null
     */
    APIRequestError getError();

    /**
     * Stops the current thread to wait for a response and returns it when it arrived
     * @return The respoonse to the API request
     * @throws APIRequestError An APIRequestError might be thrown if the API request is not valid
     */
    T awaitResponse() throws APIRequestError;

    /**
     * @param listener The given listener will not be notified about the APIRequest's response
     * @return The same APIRequestFuture this call is made on, for chaining statements
     */
    APIRequestFuture<T> removeListener(APIRequestFutureListener<T> listener);

    /**
     * @param errorHandler The given error handler will not be notified about an error returned to the APIRequest
     * @return The same APIRequestFuture this call is made on, for chaining statements
     */
    APIRequestFuture<T> removeErrorHandler(APIRequestErrorHandler errorHandler);

    /**
     * @return All listeners listening to this future
     */
    Collection<APIRequestFutureListener<T>> getListeners();

    /**
     * @return All error handlers listening to this future
     */
    Collection<APIRequestErrorHandler> getErrorHandlers();
}