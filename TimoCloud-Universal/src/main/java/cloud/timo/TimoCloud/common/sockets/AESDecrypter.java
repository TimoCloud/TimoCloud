package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.encryption.AESEncryptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.List;

@RequiredArgsConstructor
public class AESDecrypter extends ByteToMessageDecoder {

    private final SecretKey aesKey;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        out.add(Unpooled.wrappedBuffer(AESEncryptionUtil.decrypt(aesKey, bytes)));
    }

}
