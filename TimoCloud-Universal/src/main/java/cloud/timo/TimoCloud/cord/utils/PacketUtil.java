package cloud.timo.TimoCloud.cord.utils;

import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.cord.objects.ConnectionState;
import cloud.timo.TimoCloud.cord.sockets.ProxyDownstreamHandler;
import cloud.timo.TimoCloud.cord.sockets.ProxyUpstreamHandler;
import cloud.timo.TimoCloud.lib.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;

public class PacketUtil {

    public final static AttributeKey<ConnectionState> CONNECTION_STATE = AttributeKey.valueOf("connectionstate");
    public final static AttributeKey<ProxyUpstreamHandler> UPSTREAM_HANDLER = AttributeKey.valueOf("upstreamhandler");
    public final static AttributeKey<ProxyDownstreamHandler> DOWNSTREAM_HANDLER = AttributeKey.valueOf("downstreamhandler");

    public static ByteBuf createStatusPacket(ProxyGroupObject proxyGroupObject, int protocolVersion) {
        ByteBuf buf = Unpooled.buffer();
        writeVarInt(0, buf);
        writeString(Message.create()
                .set("version", Message.create()
                        .set("name", "TimoCloudCord")
                        .set("protocol", protocolVersion)
                        .toJson())
                .set("players", Message.create()
                        .set("max", proxyGroupObject.getMaxPlayerCount())
                        .set("online", proxyGroupObject.getOnlinePlayerCount())
                        .toJson())
                .set("description", Message.create()
                        .set("text", proxyGroupObject.getMotd())
                        .toJson())
                .toString(), buf);
        return buf;
    }

    public static ByteBuf kick(String text) {
        ByteBuf buf = Unpooled.buffer();
        writeVarInt(2 + text.length(), buf);
        writeVarInt(0, buf);
        writeString(text, buf);
        return buf;
    }

    public static boolean isCompressed(ByteBuf buf) {
        ByteBuf clone = Unpooled.copiedBuffer(buf);
        try {
            final int packetLength = readVarInt(clone);
            final int packetID = readVarInt(clone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void releaseByteBuf(ByteBuf buf) {
        buf.readBytes(buf.readableBytes());
        buf.release();
    }

    public static int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public static int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > maxBytes) throw new RuntimeException("VarInt too big");
            if ((in & 0x80) != 0x80) break;
        }
        return out;
    }

    public static String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        byte[] b = new byte[len];
        buf.readBytes(b);
        return new String(b);
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while (true) {
            part = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            output.writeByte(part);
            if (value == 0) break;
        }
    }

    public static void writeString(String s, ByteBuf buf) {
        byte[] b = s.getBytes();
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) low = low | 0x8000;
        buf.writeShort(low);
        if (high != 0) buf.writeByte(high);
    }

}
