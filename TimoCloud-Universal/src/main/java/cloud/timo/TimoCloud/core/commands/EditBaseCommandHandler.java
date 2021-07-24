package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.Base;

public class EditBaseCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if (args.length < 3) {
            notEnoughArgs(sender, "editbase <name> <name (String) | maxRam (int) | keepFreeRam (int) | maxCpuLoad (double)> <value>");
            return;
        }
        String baseName = args[0];
        Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByName(baseName);
        if (base == null) {
            sender.sendError("Base " + baseName + " not found. Get a list of all bases with 'listbases'");
            return;
        }
        String key = args[1];
        String value = args[2];
        switch (key.toLowerCase()) {
            case "name":
                baseName = base.getName();
                base.setName(value);
                break;
            case "maxram":
                int maxRam = Integer.parseInt(value);
                base.setMaxRam(maxRam);
                break;
            case "keepfreeram":
                int keepFreeRam = Integer.parseInt(value);
                base.setKeepFreeRam(keepFreeRam);
                break;
            case "maxcpuload":
                double maxCpuLoad = Double.parseDouble(value);
                base.setMaxCpuLoad(maxCpuLoad);
                break;
            default:
                notEnoughArgs(sender, "editbase <name> <name (String) | maxRam (int) | keepFreeRam (int) | maxCpuLoad (int)> <value>");
                return;
        }
        TimoCloudCore.getInstance().getInstanceManager().saveBases();
        sender.sendMessage("&2Base &e" + baseName + " &2has successfully been edited. New data: ");
        displayBase(base, sender);
    }
}
