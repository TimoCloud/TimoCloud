package at.TimoCraft.TimoCloud.api.helpers;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SerializationHelper {

    public String serialize(Object o) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.close();
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
    }

    public static <T> T deserialize(String serialized, T type) {
        try {
            return (T) new ObjectInputStream(new ByteArrayInputStream(serialized.getBytes(StandardCharsets.UTF_8.name()))).readObject();
        } catch (Exception e) {
            System.err.println("Error while deserializing GroupObject. Serialized string: '" + serialized + "'");
            e.printStackTrace();
            return null;
        }
    }
}
