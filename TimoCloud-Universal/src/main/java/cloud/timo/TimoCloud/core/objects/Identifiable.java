package cloud.timo.TimoCloud.core.objects;

import java.security.PublicKey;

public interface Identifiable {

    String getName();

    String getId();

    PublicKey getPublicKey();

}
