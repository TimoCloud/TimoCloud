package cloud.timo.TimoCloud.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ServerToGroupUtil {

    public String getGroupByServer(String server) {
        if (!server.contains("-")) {
            return server;
        }
        StringBuilder sb = new StringBuilder();
        String[] split = server.split("-");
        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i]);
            if (i < split.length - 2) {
                sb.append("-");
            }
        }
        return sb.toString();
    }
}
