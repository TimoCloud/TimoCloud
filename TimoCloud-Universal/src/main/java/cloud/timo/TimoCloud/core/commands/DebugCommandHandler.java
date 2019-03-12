package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.common.debugger.DataCollector;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.core.TimoCloudCore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugCommandHandler implements CommandHandler {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");


    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        try {
            Message jsonObject = DataCollector.collectData(TimoCloudCore.getInstance());
            TimoCloudCore.getInstance().getFileManager().saveJson(jsonObject, new File(
                    TimoCloudCore.getInstance().getFileManager().getDebugDirectory(), DATE_FORMAT.format(new Date()) + ".json"));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("An error occured while collecting debugging data: ");
            TimoCloudCore.getInstance().severe(e);
        }
    }

}
