package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;

public class RSAHandshakeHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final Channel channel;
    private final KeyPair keyPair;
    private final RSAHandshakeFuture future;

    public RSAHandshakeHandler(Channel channel, KeyPair keyPair, RSAHandshakeFuture future) {
        this.channel = channel;
        this.keyPair = keyPair;
        this.future = future;

        channel.pipeline().addAfter("splitter", "rsaHandshakeHandler", this);
    }

    public RSAHandshakeHandler startHandshake() {
        channel.writeAndFlush(keyPair.getPublic().getEncoded());
        return this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        try {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            SecretKey secretKey = new SecretKeySpec(RSAKeyUtil.decrypt(keyPair.getPrivate(), bytes), "AES");
            channel.pipeline().remove(this);
            future.onCompletion(secretKey);
        } catch (Exception ignored) {
        }
    }

    public interface RSAHandshakeFuture {

        void onCompletion(SecretKey aesKey);

    }
}
