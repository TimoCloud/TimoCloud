package cloud.timo.TimoCloud.utils;

public class ServerToGroupUtil {
    public static String getGroupByServer(String server) {
        if (!server.contains("-")) {
            return server;
        }
        String ret = "";
        String[] split = server.split("-");
        for (int i = 0; i < split.length - 1; i++) {
            ret = ret + split[i];
            if (i < split.length - 2) {
                ret = ret + "-";
            }
        }
        return ret;
    }
}
