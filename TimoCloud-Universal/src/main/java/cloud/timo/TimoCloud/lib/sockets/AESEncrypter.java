package cloud.timo.TimoCloud.lib.sockets;

import cloud.timo.TimoCloud.lib.encryption.AESEncryptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.SecretKey;

public class AESEncrypter extends MessageToByteEncoder<ByteBuf> {

    private final SecretKey aesKey;

    public AESEncrypter(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        byte[] inBytes = new byte[in.readableBytes()];
        in.readBytes(inBytes);
        out.writeBytes(AESEncryptionUtil.encrypt(aesKey, inBytes));
    }

}
