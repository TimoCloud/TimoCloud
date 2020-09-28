package cloud.timo.TimoCloud.bukkit.sockets.handler;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;

public class BukkitServerExecuteCommandHandler extends MessageHandler {
    public BukkitServerExecuteCommandHandler() {
        super(MessageType.SERVER_EXECUTE_COMMAND);
    }

    @Override
    public void execute(Message message, Channel channel) {
        Bukkit.getScheduler().runTask(TimoCloudBukkit.getInstance(), () -> TimoCloudBukkit.getInstance().getServer().dispatchCommand(TimoCloudBukkit.getInstance().getServer().getConsoleSender(), (String) message.getData()));
    }
}
