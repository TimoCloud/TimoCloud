package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.core.TimoCloudCore;

import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TemplateManager {

    public void zipFiles(Collection<File> files, File output, String ... entries) throws IOException {
        FileOutputStream fos = new FileOutputStream(output);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (String entry : entries) zos.putNextEntry(new ZipEntry(entry));
        for (File file : files) addFile(file, zos);
        zos.close();
        fos.close();
    }

    private static void addFile(File file, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
        fis.close();
    }
}
