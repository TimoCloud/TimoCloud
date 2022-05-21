package cloud.timo.TimoCloud.api.core.commands;

public interface CommandSender {

    /**
     * Send a message to the command sender (log level: INFO)
     *
     * @param message A message - color-formatted with minecraft color codes (see https://minecraft.gamepedia.com/Formatting_codes). Section sign (indicates a color code): '&'
     */
    void sendMessage(String message);

    /**
     * Send an error to the command sender (log level: SEVERE)
     *
     * @param message A message - color-formatted with minecraft color codes (see https://minecraft.gamepedia.com/Formatting_codes). Section sign (indicates a color code): '&'. Red by default
     */
    void sendError(String message);
}
