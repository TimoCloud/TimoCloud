package cloud.timo.TimoCloud.common.encryption;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
public class RSAKeyUtil {

    private final int KEY_SIZE = 2048;

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.genKeyPair();
        } catch (Exception e) {
            TimoCloudLogger.getLogger().severe(e);
            return null;
        }
    }

    public byte[] encrypt(PublicKey publicKey, byte[] bytes) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(bytes);
    }

    public byte[] decrypt(PrivateKey privateKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }

    public PublicKey publicKeyFromBase64(String publicKeyString) throws Exception {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString)));
    }

    public String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    };

    public PrivateKey privateKeyFromBase64(String privateKeyString) throws Exception {
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString)));
    }

    public String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    };

}
