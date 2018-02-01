package cloud.timo.TimoCloud.utils;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashUtil {

    private static final String HASH_ALGORITHM = "MD5";

    public static List<String> getDifferentFiles(String prefix, JSONObject a, JSONObject b) {
        List<String> differences = new ArrayList<>();
        for (Object key : a.keySet()) {
            String name = (String) key;
            String newName = prefix + File.separator + name;
            if (! b.containsKey(key)) {
                differences.add(newName);
                continue;
            }
            if (a.get(key) instanceof JSONObject != b.get(key) instanceof JSONObject) {
                differences.add(newName);
            }
            if (a.get(key) instanceof JSONObject) differences.addAll(getDifferentFiles(newName, (JSONObject) a.get(key), (JSONObject) b.get(key)));
            else if (! a.get(key).equals(b.get(key)));
        }
        for (Object key : b.keySet()) {
            if (! a.containsKey(key)) {
                differences.add(prefix + File.separator + key);
            }
        }
        return differences;
    }

    public static JSONObject getHashes(File file) throws IOException {
        Map<String, Object> layer = new HashMap<>();
        for (File file1 : file.listFiles()) {
            layer.put(file1.getName(), file1.isDirectory() ? getHashes(file1) : getFileHash(file1));
        }
        return new JSONObject(layer);
    }

    public static String getFileOrDirectoryHash(File directory) throws IOException {
        StringBuilder hashes = new StringBuilder();
        if (!directory.isDirectory()) return getFileHash(directory);
        for (File file : directory.listFiles()) {
            hashes.append(getFileHash(file));
        }
        return getStringHash(hashes.toString());
    }

    private static String getFileHash(File file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            new DigestInputStream(Files.newInputStream(file.toPath()), md);
            return bytesToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringHash(String string) {
        return bytesToString(getAlgorithm().digest(string.getBytes()));
    }

    private static String bytesToString(byte[] b) {
        return new String(b, StandardCharsets.UTF_8);
    }

    private static MessageDigest getAlgorithm() {
        try {
            return MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
