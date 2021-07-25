package cloud.timo.TimoCloud.common.utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;

@UtilityClass
public class FileAttributeUtil {

    public void setAttribute(Path path, String attribute, String value) throws IOException {
        if (!path.toFile().exists()) return;
        if (!Files.getFileStore(path).supportsFileAttributeView(UserDefinedFileAttributeView.class)) return;
        final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        final ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        view.write(attribute, writeBuffer);
    }

    public String getAttribute(Path path, String attribute) throws IOException {
        if (!path.toFile().exists()) return null;
        if (!Files.getFileStore(path).supportsFileAttributeView(UserDefinedFileAttributeView.class)) return "";
        final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        final ByteBuffer readBuffer = ByteBuffer.allocate(view.size(attribute));
        view.read(attribute, readBuffer);
        readBuffer.flip();
        return new String(readBuffer.array(), StandardCharsets.UTF_8);
    }
}
