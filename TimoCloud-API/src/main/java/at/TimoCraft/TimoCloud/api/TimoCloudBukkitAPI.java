package at.TimoCraft.TimoCloud.api;

import at.TimoCraft.TimoCloud.api.implementations.GroupObjectBasicImplementation;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Use {@link TimoCloudAPI#getBukkitInstance()} to get an instance of this class
 */
public interface TimoCloudBukkitAPI {

    /**
     *
     * @return The server you are on as ServerObject
     */
    ServerObject getThisServer();

    /**
     * The given command will be executed on BungeeCord
     * @param command Without leading '/'
     */
    void sendCommandToBungeeCord(String command);
}
