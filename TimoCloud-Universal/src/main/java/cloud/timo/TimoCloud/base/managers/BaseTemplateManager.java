package cloud.timo.TimoCloud.base.managers;

import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BaseTemplateManager {

    private static final int BUFFER = 1024;

    public void extractFiles(InputStream inputStream, File destination) throws IOException {
        destination.mkdirs();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            File file = new File(destination, zipEntry.getName());
            if (zipEntry.getName().endsWith("/")) {
                file.mkdirs();
                continue;
            }
            if (file.exists()) {
                if (file.isDirectory()) FileDeleteStrategy.FORCE.deleteQuietly(file);
                else Files.delete(file.toPath());
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            while (zipInputStream.available() > 0) {
                byte[] bytes = new byte[BUFFER];
                int readCount = zipInputStream.read(bytes, 0, BUFFER);
                if (readCount <= 0) continue;
                fos.write(bytes, 0, readCount);
            }

            fos.close();
            file.setLastModified(zipEntry.getTime());
            zipInputStream.closeEntry();
        }
        zipInputStream.close();
    }

}
