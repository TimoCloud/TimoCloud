package cloud.timo.TimoCloud.core.objects;

import java.security.PublicKey;

public interface PublicKeyIdentifiable extends Identifiable {

    PublicKey getPublicKey();

}
