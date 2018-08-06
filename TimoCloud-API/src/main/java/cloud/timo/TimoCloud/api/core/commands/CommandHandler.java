package cloud.timo.TimoCloud.api.core.commands;

public interface CommandHandler {

    void onCommand(String command, CommandSender sender, String ... args);
}
