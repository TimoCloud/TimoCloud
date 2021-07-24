package cloud.timo.TimoCloud.api.async;

import java.util.Collection;
import java.util.Collections;

public class APIRequestError extends RuntimeException {

    private Integer code;
    private String message;
    private final Collection<?> arguments;

    public APIRequestError(String message) {
        super(message);
        this.arguments = Collections.emptyList();
    }

    public APIRequestError(String message, int code) {
        this(message, code, Collections.emptyList());
    }

    public APIRequestError(String message, int code, Collection<?> arguments) {
        super(String.format("[%d] %s", code, message));
        this.code = code;
        this.message = message;
        this.arguments = arguments;
    }

    public Integer getErrorCode() {
        return this.code;
    }

    public String getErrorMessage() {
        return this.message;
    }

    public Collection<?> getArguments() {
        return arguments;
    }

    public String getFormattedErrorMessage() {
        return String.format("[%d] %s", getErrorCode(), getErrorMessage());
    }

}
