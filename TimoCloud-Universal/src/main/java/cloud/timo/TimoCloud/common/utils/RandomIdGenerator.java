package cloud.timo.TimoCloud.common.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Random;

@UtilityClass
public class RandomIdGenerator {

    private final String ID_CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPRSTUVWXYZ123456789";

    private final int DEFAULT_ID_LENGTH = 6;
    private final Random random = new SecureRandom();

    public String generateId(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(ID_CHARS.charAt(random.nextInt(ID_CHARS.length())));
        }
        return stringBuilder.toString();
    }

    public String generateId() {
        return generateId(DEFAULT_ID_LENGTH);
    }

}
