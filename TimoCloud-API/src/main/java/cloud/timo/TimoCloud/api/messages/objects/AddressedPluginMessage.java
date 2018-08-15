package cloud.timo.TimoCloud.api.messages.objects;

import cloud.timo.TimoCloud.api.TimoCloudAPI;

/**
 * An addressed plugin message contains a {@link PluginMessage} together with the message's sender and recipient
 * You can compare it with a letter in an envelope, where the letter itself is the {@link PluginMessage}, and the {@link AddressedPluginMessage} is the envelope which contains the letter and information about the sender and the recipient.
 */
public class AddressedPluginMessage {

    private MessageClientAddress sender;
    private MessageClientAddress recipient;
    private PluginMessage message;

    public AddressedPluginMessage(MessageClientAddress recipient, PluginMessage message) {
        this(TimoCloudAPI.getMessageAPI().getOwnAddress(), recipient, message);
    }

    public AddressedPluginMessage(MessageClientAddress sender, MessageClientAddress recipient, PluginMessage message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    public MessageClientAddress getSender() {
        return sender;
    }

    public MessageClientAddress getRecipient() {
        return recipient;
    }

    public PluginMessage getMessage() {
        return message;
    }

}
