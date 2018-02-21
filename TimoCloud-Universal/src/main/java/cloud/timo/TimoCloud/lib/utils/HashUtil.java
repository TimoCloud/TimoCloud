package cloud.timo.TimoCloud.lib.utils;

import org.apache.commons.io.FileDeleteStrategy;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.CRC32;

public class HashUtil {

    private static final String HASH_ALGORITHM = "MD5";

    private static final List<String> IGNORE_NAMES = Arrays.asList(".DS_Store");

    public static List<String> getDifferentFiles(String prefix, JSONObject a, JSONObject b) {
        List<String> differences = new ArrayList<>();
        for (Object key : a.keySet()) {
            String name = (String) key;
            boolean cont = false;
            for (String ignore : IGNORE_NAMES) if (name.endsWith(ignore)) cont = true;
            if (cont) continue;
            String newName = prefix + File.separator + name;
            if (!b.containsKey(key)) {
                differences.add(newName);
                continue;
            }
            if (a.get(key) instanceof JSONObject != b.get(key) instanceof JSONObject) {
                differences.add(newName);
                continue;
            }
            if (a.get(key) instanceof JSONObject)
                differences.addAll(getDifferentFiles(newName, (JSONObject) a.get(key), (JSONObject) b.get(key)));
            else if (!a.get(key).equals(b.get(key))) differences.add(newName);
        }
        for (Object key : b.keySet()) {
            String name = (String) key;
            boolean cont = false;
            for (String ignore : IGNORE_NAMES) if (name.endsWith(ignore)) cont = true;
            if (cont) continue;
            if (!a.containsKey(key)) {
                differences.add(prefix + File.separator + key);
            }
        }
        return differences;
    }

    public static void deleteIfNotExisting(File base, String prefix, JSONObject a, JSONObject b) throws IOException {
        for (Object key : a.keySet()) {
            if (!b.containsKey(key)) {
                File file = new File(base, prefix + "/" + key);
                if (a.get(key) instanceof JSONObject) FileDeleteStrategy.FORCE.deleteQuietly(file);
                else Files.delete(file.toPath());
            } else {
                if (a.get(key) instanceof JSONObject && b.get(key) instanceof JSONObject) {
                    deleteIfNotExisting(base, prefix + "/" + key, (JSONObject) a.get(key), (JSONObject) b.get(key));
                }
            }
        }
    }

    public static JSONObject getHashes(File file) throws IOException {
        Map<String, Object> layer = new HashMap<>();
        for (File file1 : file.listFiles()) {
            if (!file1.isDirectory() && IGNORE_NAMES.contains(file1.getName())) continue;
            layer.put(file1.getName(), file1.isDirectory() ? getHashes(file1) : getFileHash(file1));
        }
        return new JSONObject(layer);
    }

    private static String getFileHash(File file) throws IOException {
        CRC32 crc = new CRC32();
        crc.update(Files.readAllBytes(file.toPath()));
        return crc.getValue() + "";
    }

    private static String bytesToString(byte[] b) {
        return new String(b, StandardCharsets.UTF_8);
    }

}
