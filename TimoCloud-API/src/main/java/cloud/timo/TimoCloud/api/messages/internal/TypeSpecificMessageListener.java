package cloud.timo.TimoCloud.api.messages.internal;

import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TypeSpecificMessageListener {

    private MessageListener listener;
    private Set<String> supportedTypes;

    public TypeSpecificMessageListener(MessageListener listener, String ... supportedTypes) {
        this.listener = listener;
        this.supportedTypes = new HashSet<>(Arrays.asList(supportedTypes));
    }

    public MessageListener getListener() {
        return listener;
    }

    public Set<String> getSupportedTypes() {
        return supportedTypes;
    }

    public boolean isTypeSupported(String type) {
        return getSupportedTypes().isEmpty() || getSupportedTypes().contains(type);
    }
}
