package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.core.TimoCloudCore;

import java.security.PublicKey;
import java.util.Collection;
import java.util.HashSet;

public class CorePublicKeyManager {

    private Collection<PublicKey> permittedBaseKeys; // One-time keys for first connection

    public CorePublicKeyManager() {
        this.permittedBaseKeys = new HashSet<>();
    }

    public void addPermittedBaseKey(PublicKey publicKey) {
        permittedBaseKeys.add(publicKey);
    }

    public void removePermittedBaseKey(PublicKey publicKey) {
        permittedBaseKeys.remove(publicKey);
    }

    public boolean isBaseKeyPermitted(PublicKey publicKey) {
        return permittedBaseKeys.contains(publicKey) || TimoCloudCore.getInstance().getInstanceManager().getBaseByPublicKey(publicKey) != null;
    }

    public boolean redeemBaseKeyIfPermitted(PublicKey publicKey) {
        if (!isBaseKeyPermitted(publicKey)) return false;
        permittedBaseKeys.remove(publicKey);
        return true;
    }

    public boolean isServerKeyPermitted(PublicKey publicKey) {
        return TimoCloudCore.getInstance().getInstanceManager().getServerByPublicKey(publicKey) != null;
    }

    public boolean isProxyKeyPermitted(PublicKey publicKey) {
        return TimoCloudCore.getInstance().getInstanceManager().getProxyByPublicKey(publicKey) != null;
    }

    public boolean isKeyPermitted(PublicKey publicKey) {
        return isBaseKeyPermitted(publicKey) || isServerKeyPermitted(publicKey) || isProxyKeyPermitted(publicKey);
    }

    public boolean redeemKeyIfPermitted(PublicKey publicKey) {
        if (!isKeyPermitted(publicKey)) return false;
        permittedBaseKeys.remove(publicKey);
        return true;
    }

}
