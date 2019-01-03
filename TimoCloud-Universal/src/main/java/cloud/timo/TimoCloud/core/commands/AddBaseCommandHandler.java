package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.lib.encryption.RSAKeyUtil;

import java.security.PublicKey;

public class AddBaseCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if (args.length < 1) {
            notEnoughArgs(sender, "addbase <publicKey>");
            return;
        }
        String publicKeyString = args[0];
        try {
            PublicKey publicKey = RSAKeyUtil.publicKeyFromBase64(publicKeyString);
            TimoCloudCore.getInstance().getCorePublicKeyManager().addPermittedBaseKey(publicKey);
            sender.sendMessage("The public key has been permitted, so your base may connect now.");
        } catch (Exception e) {
            sender.sendError(String.format("Invalid public key: '%s'. Please use the public key the base told you when you first started it. It is stored in the base/keys/public.tck.", publicKeyString));
        }
    }

}
