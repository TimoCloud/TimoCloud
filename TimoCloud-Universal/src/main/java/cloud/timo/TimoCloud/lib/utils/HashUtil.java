package cloud.timo.TimoCloud.lib.utils;

import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.CRC32;

public class HashUtil {

    private static final Set<String> IGNORE_NAMES = new HashSet<>(Arrays.asList(".DS_Store"));

    public static List<String> getDifferentFiles(String prefix, Map<String, Object> a, Map<String, Object> b) {
        List<String> differences = new ArrayList<>();
        for (Object key : a.keySet()) {
            String name = (String) key;
            boolean cont = false;
            for (String ignore : IGNORE_NAMES) if (name.endsWith(ignore)) cont = true;
            if (cont) continue;
            if (prefix.endsWith("/")) prefix = prefix.substring(0, prefix.length()-1);
            String newName = prefix + File.separator + name;
            if (!b.containsKey(key)) {
                differences.add(newName);
                continue;
            }
            if (a.get(key) instanceof Map != b.get(key) instanceof Map) {
                differences.add(newName);
                continue;
            }
            if (a.get(key) instanceof Map)
                differences.addAll(getDifferentFiles(newName, (Map<String, Object>) a.get(key), (Map<String, Object>) b.get(key)));
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

    public static void deleteIfNotExisting(File base, String prefix, Map<String, Object> a, Map<String, Object> b) throws IOException {
        for (Object key : a.keySet()) {
            if (!b.containsKey(key)) {
                File file = new File(base, prefix + "/" + key);
                if (a.get(key) instanceof Map) FileDeleteStrategy.FORCE.deleteQuietly(file);
                else Files.delete(file.toPath());
            } else {
                if (a.get(key) instanceof Map && b.get(key) instanceof Map) {
                    deleteIfNotExisting(base, prefix + "/" + key, (Map<String, Object>) a.get(key), (Map<String, Object>) b.get(key));
                }
            }
        }
    }

    public static Map<String, Object> getHashes(File file) throws IOException {
        if (! (file.exists() && file.isDirectory())) return new HashMap<>();
        Map<String, Object> layer = new HashMap<>();
        for (File file1 : file.listFiles()) {
            if (!file1.isDirectory() && IGNORE_NAMES.contains(file1.getName())) continue;
            layer.put(file1.getName() + (file1.isDirectory() ? "/" : ""), file1.isDirectory() ? getHashes(file1) : getFileHash(file1));
        }
        return layer;
    }

    private static String getFileHash(File file) throws IOException {
        CRC32 crc = new CRC32();
        Path path = file.toPath();
        crc.update(Files.readAllBytes(path));
        return crc.getValue() + "";
    }

    private static String bytesToString(byte[] b) {
        return new String(b, StandardCharsets.UTF_8);
    }

}
