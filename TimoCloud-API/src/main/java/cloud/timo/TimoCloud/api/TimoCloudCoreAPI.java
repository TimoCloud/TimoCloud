package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;

public interface TimoCloudCoreAPI {

    /**
     * Register a command handler. It will get notified about commands entered in the Core console or ingame.
     *
     * @param commandHandler The handler which shall get notified about commands
     * @param commands       The commands the handler shall be notified about
     */
    void registerCommandHandler(CommandHandler commandHandler, String... commands);

    /**
     * Unregister a command handler. It will no longer be notified about commands.
     * You should call this method in your plugin's onDisable method
     *
     * @param commands Unregisters all of the handlers assigned to these commands
     */
    void unregisterCommandHandler(String... commands);

}
