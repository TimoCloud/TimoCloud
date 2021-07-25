package cloud.timo.TimoCloud.core.managers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TemplateManager {

    private static void addFile(File file, File base, ZipOutputStream zos) {
        if (file.isDirectory() && file.listFiles().length > 0) {
            for (File file1 : file.listFiles()) addFile(file1, base, zos);
            return;
        }
        try {
            if (!file.exists()) return;
            String relative = base.toURI().relativize(file.toURI()).getPath();
            if (file.isDirectory() && !relative.endsWith("/")) relative += "/";
            ZipEntry zipEntry = new ZipEntry(relative);
            zos.putNextEntry(zipEntry);
            if (!file.isDirectory()) {
                Files.copy(file.toPath(), zos);
            }
            zipEntry.setTime(file.lastModified());
            zos.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zipFiles(Collection<File> files, File base, OutputStream output) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(output);
        for (File file : files) addFile(file, base, zos);
        zos.close();
        output.flush();
    }
}
