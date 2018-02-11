package cloud.timo.TimoCloud.sockets;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BasicStringHandler extends SimpleChannelInboundHandler<String> {

    private StringBuilder parsed;
    private int open = 0;
    private boolean isString = false;

    public BasicStringHandler() {
        parsed = new StringBuilder();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        read(message);
    }

    public void read(String message) {
        for (String c : message.split("")) {
            parsed.append(c);
            if (c.equals("\"") && (parsed.length() < 2 || !Character.toString(parsed.charAt(parsed.length() - 2)).equals("\\"))) isString = !isString;
            if (isString) continue;
            if (c.equals("{")) open++;
            if (c.equals("}")) {
                open--;
                if (open == 0) {
                    try {
                        handleJSON((JSONObject) JSONValue.parse(parsed.toString()), parsed.toString());
                    } catch (Exception e) {
                        System.err.println("Error while parsing JSON: ");
                        e.printStackTrace();
                    }
                    parsed = new StringBuilder();
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message) {}
}
