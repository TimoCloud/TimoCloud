package cloud.timo.TimoCloud.api.messages.exceptions;

public class MalformedAddressException extends Exception {

    public MalformedAddressException(String address) {
        super("Malformed message address: " + address + ". (Should be of format name@TYPE)");
    }
}
