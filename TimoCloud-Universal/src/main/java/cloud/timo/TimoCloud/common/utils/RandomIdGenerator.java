package cloud.timo.TimoCloud.common.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomIdGenerator {

    private static final String ID_CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPRSTUVWXYZ123456789";


    private static final int DEFAULT_ID_LENGTH = 6;
    private static final Random random = new SecureRandom();

    public static String generateId(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<length; i++) {
            stringBuilder.append(ID_CHARS.charAt(random.nextInt(ID_CHARS.length())));
        }
        return stringBuilder.toString();
    }

    public static String generateId() {
        return generateId(DEFAULT_ID_LENGTH);
    }

}
