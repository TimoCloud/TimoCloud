package cloud.timo.TimoCloud.lib.sockets;

import cloud.timo.TimoCloud.lib.encryption.AESEncryptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.crypto.SecretKey;
import java.util.List;

public class AESDecrypter extends ByteToMessageDecoder {

    private SecretKey aesKey;

    public AESDecrypter(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        out.add(Unpooled.wrappedBuffer(AESEncryptionUtil.decrypt(aesKey, bytes)));
    }

}
