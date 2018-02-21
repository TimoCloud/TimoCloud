package cloud.timo.TimoCloud.lib.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class FileAttributeUtil {

    public static void setAttribute(Path path, String attribute, String value) throws IOException {
        if (! path.toFile().exists()) return;
        if (! Files.getFileStore(path).supportsFileAttributeView(UserDefinedFileAttributeView.class)) return;
        final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        final byte[] bytes = value.getBytes("UTF-8");
        final ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        view.write(attribute, writeBuffer);
    }

    public static String getAttribute(Path path, String attribute) throws IOException {
        if (! path.toFile().exists()) return null;
        if (! Files.getFileStore(path).supportsFileAttributeView(UserDefinedFileAttributeView.class)) return "";
        final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        final ByteBuffer readBuffer = ByteBuffer.allocate(view.size(attribute));
        view.read(attribute, readBuffer);
        readBuffer.flip();
        return new String(readBuffer.array(), "UTF-8");
    }
}
