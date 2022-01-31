package cloud.timo.TimoCloud.base.sockets.handler;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import java.util.Objects;

public class BasePIDExistRequest extends MessageHandler {
    public BasePIDExistRequest() {
        super(MessageType.BASE_PID_EXIST_REQUEST);
    }

    @Override
    public void execute(Message message, Channel channel) {
        String id = (String) message.get("id");
        int pid = ((Number) message.get("pid")).intValue();
        SystemInfo si = new SystemInfo();
        final OSProcess process = si.getOperatingSystem().getProcess(pid);
        TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create()
                .setType(MessageType.BASE_PID_EXIST_RESPONSE)
                .setTarget(id)
                .set("requestedPid", pid)
                .set("running", Objects.nonNull(process))
        );
    }
}
