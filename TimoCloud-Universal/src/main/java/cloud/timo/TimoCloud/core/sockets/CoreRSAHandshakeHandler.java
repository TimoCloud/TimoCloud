package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.common.encryption.AESEncryptionUtil;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.sockets.AESDecrypter;
import cloud.timo.TimoCloud.common.sockets.AESEncrypter;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Timer;
import java.util.TimerTask;

public class CoreRSAHandshakeHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static final AttributeKey<PublicKey> RSA_KEY_ATTRIBUTE_KEY = AttributeKey.valueOf("RSA_KEY");
    public static final AttributeKey<SecretKey> AES_KEY_ATTRIBUTE_KEY = AttributeKey.valueOf("AES_KEY");
    public static final AttributeKey<Boolean> HANDSHAKE_PERFORMED_ATTRIBUTE_KEY = AttributeKey.valueOf("HANDSHAKE_PERFORMED");

    private final KeyFactory keyFactory;

    public CoreRSAHandshakeHandler() throws Exception {
        this.keyFactory = KeyFactory.getInstance("RSA");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        Channel channel = ctx.channel();
        try {
            if (TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel) != null) return;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
            if (! TimoCloudCore.getInstance().getCorePublicKeyManager().isKeyPermitted(publicKey)) {
                channel.close();
                return;
            }
            channel.attr(RSA_KEY_ATTRIBUTE_KEY).set(publicKey);
            SecretKey aesKey = AESEncryptionUtil.generateAESKey();
            channel.attr(AES_KEY_ATTRIBUTE_KEY).set(aesKey);
            channel.writeAndFlush(RSAKeyUtil.encrypt(publicKey, aesKey.getEncoded())); // Send AES key encrypted with the received public key. If the client is really permitted (possesses the corresponding private key), it is able to decrypt the AES key and send protocol encrypted with it
            channel.pipeline().addBefore("prepender", "decrypter", new AESDecrypter(aesKey));
            channel.pipeline().addBefore("prepender", "decoder", new StringDecoder(CharsetUtil.UTF_8));
            channel.pipeline().addBefore("prepender", "handler", TimoCloudCore.getInstance().getStringHandler());
            channel.pipeline().addLast("encrypter", new AESEncrypter(aesKey));
            channel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

            channel.pipeline().remove(this);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (! channel.isOpen()) return;
                    if (! channel.hasAttr(HANDSHAKE_PERFORMED_ATTRIBUTE_KEY)) {
                        channel.close();
                        return;
                    }
                    if (! channel.attr(HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).get()) channel.close(); // If the client did not perform the handshake within 5 seconds, it probably was not permitted to connect since it was not able to decrypt the AES key and perform the handshake
                }
            }, 5000);
        } catch (Exception e) {
            channel.close();
        }
    }

}
