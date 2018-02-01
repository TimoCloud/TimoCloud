package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

@ChannelHandler.Sharable
public class BaseFileChunkHandler extends SimpleChannelInboundHandler<ChunkedFile> {

    private FileOutputStream fileOutputStream;
    private File file;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChunkedFile chunkedFile) throws Exception {
        if (fileOutputStream == null) {
            File file = new File(TimoCloudBase.getInstance().getFileManager().getCacheDirectory(), new Date().getTime() + "");
            file.createNewFile();
            setFile(file);
            setFileOutputStream(new FileOutputStream(file));
        }
        ByteBuf buf = chunkedFile.readChunk(ByteBufAllocator.DEFAULT);
        int numberOfReadableBytes = buf.readableBytes();
        byte[] bytes = new byte[numberOfReadableBytes];
        buf.readBytes(bytes);
        getFileOutputStream().write(bytes, 0, bytes.length);
    }

    public FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    public void setFileOutputStream(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
