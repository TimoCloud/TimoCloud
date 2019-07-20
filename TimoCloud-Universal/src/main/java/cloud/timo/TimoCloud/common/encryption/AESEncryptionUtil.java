package cloud.timo.TimoCloud.common.encryption;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESEncryptionUtil {

    private static final String CIPHER_IDENTIFIER = "AES/CBC/PKCS5PADDING";
    private static final int IV_LENGTH = 16;
    private static final int AES_KEY_LENGTH = 256;

    private static byte[] generateInitVector() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static SecretKey generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_KEY_LENGTH);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            TimoCloudLogger.getLogger().severe("An exception occurred while generating AES key:");
            TimoCloudLogger.getLogger().severe(e);
            return null;
        }
    }

    public static byte[] encrypt(SecretKey key, byte[] value) {
        try {
            byte[] initVector = generateInitVector();
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(),"AES");

            Cipher cipher = Cipher.getInstance(CIPHER_IDENTIFIER);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] enrypted = cipher.doFinal(value);
            byte[] ret = new byte[IV_LENGTH + enrypted.length]; // Final byte array consists of init vector + encrypted bytes
            System.arraycopy(initVector, 0, ret, 0, initVector.length);
            System.arraycopy(enrypted, 0, ret, IV_LENGTH, enrypted.length);
            return ret;

        } catch (Exception e) {
            TimoCloudLogger.getLogger().severe("An exception occurred while encrypting message:");
            TimoCloudLogger.getLogger().severe(e);
            return null;
        }
    }

    public static byte[] decrypt(SecretKey key, byte[] total) {
        try {
            byte[] initVector = new byte[IV_LENGTH];
            byte[] encrypted = new byte[total.length - IV_LENGTH];
            System.arraycopy(total, 0, initVector, 0, IV_LENGTH);
            System.arraycopy(total, IV_LENGTH, encrypted, 0, encrypted.length);

            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(),"AES");

            Cipher cipher = Cipher.getInstance(CIPHER_IDENTIFIER);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            TimoCloudLogger.getLogger().severe("An exception occurred while decrypting message:");
            TimoCloudLogger.getLogger().severe(e);
            return null;
        }
    }

}
