package cloud.timo.TimoCloud.base.managers;

import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BaseTemplateManager {

    public void extractFiles(File zip, File destination) throws IOException {
        ZipFile zipFile = new ZipFile(zip);
        Enumeration<?> enu = zipFile.entries();
        while (enu.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enu.nextElement();
            File file = new File(destination, zipEntry.getName());
            if (zipEntry.getName().endsWith("/")) {
                file.mkdirs();
                continue;
            }
            file.getParentFile().mkdirs();
            if (file.exists()) {
                if (file.isDirectory()) FileDeleteStrategy.FORCE.deleteQuietly(file);
                else Files.delete(file.toPath());
            }
            file.createNewFile();
            InputStream is = zipFile.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) >= 0) fos.write(bytes, 0, length);
            is.close();
            fos.close();
            file.setLastModified(zipEntry.getTime());
        }
        zipFile.close();
    }

    public void extractFilesAndDeleteZip(File zip, File destination) throws IOException {
        extractFiles(zip, destination);
        zip.delete();
    }
}
