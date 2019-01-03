package cloud.timo.TimoCloud.lib.sockets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketLengthSplitter extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        while (byteBuf.readableBytes() > 0) {
            byteBuf.markReaderIndex();
            if (byteBuf.readableBytes() < 4) return;
            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length) { // Not all bytes received yet
                byteBuf.resetReaderIndex();
                return;
            }
            out.add(byteBuf.copy(byteBuf.readerIndex(), length).retain());
            byteBuf.skipBytes(length);
        }
    }
}
