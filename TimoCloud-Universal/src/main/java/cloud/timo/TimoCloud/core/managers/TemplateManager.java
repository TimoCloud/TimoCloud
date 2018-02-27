package cloud.timo.TimoCloud.core.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TemplateManager {

    public void zipFiles(Collection<File> files, File base, File output) throws IOException {
        FileOutputStream fos = new FileOutputStream(output);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (File file : files) addFile(file, base, zos);
        zos.close();
        fos.close();
    }

    private static void addFile(File file, File base, ZipOutputStream zos) throws IOException {
        if (file.isDirectory() && file.listFiles().length > 0) {
            for (File file1 : file.listFiles()) addFile(file1, base, zos);
            return;
        }
        try {
            if (! file.exists()) return;
            String relative = base.toURI().relativize(file.toURI()).getPath();
            if (file.isDirectory() && ! relative.endsWith("/")) relative += "/";
            ZipEntry zipEntry = new ZipEntry(relative);
            zos.putNextEntry(zipEntry);
            if (! file.isDirectory()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                fis.close();
            }
            zipEntry.setTime(file.lastModified());
            zos.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
