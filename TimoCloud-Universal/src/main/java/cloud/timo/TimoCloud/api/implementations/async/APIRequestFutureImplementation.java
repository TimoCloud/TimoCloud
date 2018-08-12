package cloud.timo.TimoCloud.api.implementations.async;

import cloud.timo.TimoCloud.api.async.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.CountDownLatch;

public class APIRequestFutureImplementation<T> implements APIRequestFuture<T> {

    private APIRequest<T> request;
    private Collection<APIRequestFutureListener<T>> listeners = new LinkedHashSet<>();
    private Collection<APIRequestErrorHandler> errorHandlers = new LinkedHashSet<>();
    private volatile T response;
    private volatile APIRequestError error;
    private volatile Boolean completed = false;
    private Boolean success;
    private volatile Collection<CountDownLatch> latches = new HashSet<>();

    public APIRequestFutureImplementation(APIRequest<T> request) {
        this.request = request;
    }

    @Override
    public APIRequestFuture<T> onCompletion(APIRequestFutureListenerWithoutParams<T> listener) {
        return onCompletionGeneral(listener);
    }

    @Override
    public APIRequestFuture<T> onCompletion(APIRequestFutureListenerWithParams<T> listener) {
        return onCompletionGeneral(listener);
    }

    private APIRequestFuture<T> onCompletionGeneral(APIRequestFutureListener<T> listener) {
        listeners.add(listener);
        if (response != null) {
            callOnCompletion(listener);
        }
        return this;
    }

    @Override
    public APIRequestFuture<T> onError(APIRequestErrorHandler handler) {
        errorHandlers.add(handler);
        if (error != null) {
            callExceptionCaught(handler);
        }
        return this;
    }

    @Override
    public APIRequestFuture<T> removeListener(APIRequestFutureListener listener) {
        listeners.remove(listener);
        return this;
    }

    @Override
    public APIRequestFuture<T> removeErrorHandler(APIRequestErrorHandler errorHandler) {
        errorHandlers.remove(errorHandler);
        return this;
    }

    @Override
    public APIRequest<T> getAPIRequest() {
        return request;
    }


    @Override
    public T getResponse() {
        return response;
    }

    @Override
    public APIRequestError getError() {
        return error;
    }

    @Override
    public T awaitResponse() throws APIRequestError {
        CountDownLatch latch = new CountDownLatch(1);
        this.latches.add(latch);
        while (latch.getCount() > 0) {
            try {
                latch.await();
            } catch (Exception e) {
                e.printStackTrace(); // This should never happen
            }
        }
        if (isSuccess()) {
            return response;
        }
        throw getError();
    }

    public void requestComplete(APIResponse<T> response) {
        this.completed = true;
        this.success = response.isSuccess();
        if (response.isSuccess()) {
            this.response = response.getData();
            for (APIRequestFutureListener<T> listener : getListeners()) {
                callOnCompletion(listener);
            }
        } else {
            this.error = response.getError();
            for (APIRequestErrorHandler handler : getErrorHandlers()) {
                callExceptionCaught(handler);
            }
        }
        notifyLatches();
    }

    @Override
    public Collection<APIRequestFutureListener<T>> getListeners() {
        return listeners;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public Boolean isSuccess() {
        return success;
    }

    @Override
    public Collection<APIRequestErrorHandler> getErrorHandlers() {
        return errorHandlers;
    }

    private void callOnCompletion(APIRequestFutureListener<T> listener) {
        try {
            if (listener instanceof APIRequestFutureListenerWithParams) {
                ((APIRequestFutureListenerWithParams<T>) listener).requestComplete(response);
            } else if (listener instanceof APIRequestFutureListenerWithoutParams) {
                ((APIRequestFutureListenerWithoutParams<T>) listener).requestComplete();
            }
        } catch (Exception e) {
            System.err.println("Error while calling method requestComplete on APIRequestFutureListener");
            e.printStackTrace();
        }
    }

    private void callExceptionCaught(APIRequestErrorHandler handler) {
        try {
            handler.exceptionCaught(error);
        } catch (Exception e) {
            System.err.println("Error while calling method exceptionCaught on APIRequestErrorHandler");
            e.printStackTrace();
        }
    }

    private void notifyLatches() {
        for (CountDownLatch latch : latches) {
            latch.countDown();
        }
        this.latches.clear();
    }
}
