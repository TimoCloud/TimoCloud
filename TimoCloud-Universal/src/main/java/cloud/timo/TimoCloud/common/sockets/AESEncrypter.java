package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.encryption.AESEncryptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;

@RequiredArgsConstructor
public class AESEncrypter extends MessageToByteEncoder<ByteBuf> {

    private final SecretKey aesKey;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        byte[] inBytes = new byte[in.readableBytes()];
        in.readBytes(inBytes);
        out.writeBytes(AESEncryptionUtil.encrypt(aesKey, inBytes));
    }

}
