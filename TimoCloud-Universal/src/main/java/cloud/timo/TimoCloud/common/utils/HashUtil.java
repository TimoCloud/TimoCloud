package cloud.timo.TimoCloud.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

@UtilityClass
public class HashUtil {

    private final Set<String> IGNORE_NAMES = new HashSet<>(Collections.singletonList(".DS_Store"));

    public List<String> getDifferentFiles(String prefix, Map<String, Object> a, Map<String, Object> b) {
        String newPrefix = prefix;

        List<String> differences = new ArrayList<>();
        for (String key : a.keySet()) {
            boolean cont = false;
            for (String ignore : IGNORE_NAMES)
                if (key.endsWith(ignore)) {
                    cont = true;
                    break;
                }

            if (cont) continue;
            if (newPrefix.endsWith("/")) newPrefix = newPrefix.substring(0, newPrefix.length()-1);

            String newName = newPrefix + File.separator + key;
            if (!b.containsKey(key) || a.get(key) instanceof Map != b.get(key) instanceof Map) {
                differences.add(newName);
            } else if (a.get(key) instanceof Map) {
                differences.addAll(getDifferentFiles(newName, (Map<String, Object>) a.get(key), (Map<String, Object>) b.get(key)));
            } else if (!a.get(key).equals(b.get(key))) {
                differences.add(newName);
            }
        }

        for (String key : b.keySet()) {
            boolean cont = false;
            for (String ignore : IGNORE_NAMES)
                if (key.endsWith(ignore)) {
                    cont = true;
                    break;
                }

            if (cont) continue;
            if (!a.containsKey(key)) {
                differences.add(newPrefix + File.separator + key);
            }
        }

        return differences;
    }

    public void deleteIfNotExisting(File base, String prefix, Map<String, Object> a, Map<String, Object> b) throws IOException {
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

    public Map<String, Object> getHashes(File file) throws IOException {
        if (!(file.exists() && file.isDirectory())) return new HashMap<>();

        Map<String, Object> layer = new HashMap<>();
        for (File file1 : file.listFiles()) {
            if (!file1.isDirectory() && IGNORE_NAMES.contains(file1.getName())) continue;
            layer.put(file1.getName() + (file1.isDirectory() ? "/" : ""), file1.isDirectory() ? getHashes(file1) : getFileHash(file1));
        }
        return layer;
    }

    private String getFileHash(File file) throws IOException {
        CRC32 crc = new CRC32();
        Path path = file.toPath();
        crc.update(Files.readAllBytes(path));
        return crc.getValue() + "";
    }

}
