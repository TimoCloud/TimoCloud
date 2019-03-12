package cloud.timo.TimoCloud.common.sockets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketLengthPrepender extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (in instanceof byte[]) {
            byte[] bytes = (byte[]) in;
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        } else if (in instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) in;
            out.writeInt(byteBuf.readableBytes());
            out.writeBytes(byteBuf);
        } else {
            throw new IllegalArgumentException("Unknown packet type: " + in.getClass().getName());
        }
    }

}
