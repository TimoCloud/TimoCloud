package cloud.timo.TimoCloud.api.messages.exceptions;

import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;

public class RecipientNotFoundException extends Exception {

    public RecipientNotFoundException(MessageClientAddress address) {
        super("Could not find message recipient with address " + address.toString());
    }
}
