package cloud.timo.TimoCloud.cord.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.cord.TimoCloudCord;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProxyManager {

    public ProxyGroupObject getProxyGroupByHostName(String hostName) {
        for (ProxyGroupObject group : TimoCloudAPI.getUniversalAPI().getProxyGroups())
            for (String hostName1 : group.getHostNames())
                if (matches(hostName, hostName1)) return group;
        return null;
    }

    private static boolean matches(String input, String pattern) {
        if (pattern.trim().equalsIgnoreCase("*")) return true;
        return createPatternFromHostName(pattern.toLowerCase().trim()).matcher(input.toLowerCase().trim()).matches();
    }

    private static Pattern createPatternFromHostName(String hostName) {
        StringBuilder sb = new StringBuilder();
        for (String part : hostName.split("\\*")) {
            if (part.length() > 0) sb.append(Pattern.quote(part));
            sb.append(".*");
        }
        return Pattern.compile(sb.toString());
    }


    public ProxyObject getFreeProxy(ProxyGroupObject group) {
        if (group.getProxyChooseStrategy() == null) {
            TimoCloudCord.getInstance().severe("Error while choosing proxy: ProxyChooseStrategy of group '" + group.getName() + "' is null. Please report this.");
            return null;
        }
        List<ProxyObject> proxies = group.getProxies().stream().filter(proxy -> proxy.getOnlinePlayerCount() < proxy.getGroup().getMaxPlayerCountPerProxy()).collect(Collectors.toList());
        proxies.sort(Comparator.comparing(ProxyObject::getOnlinePlayerCount));
        if (proxies.size() == 0) return null;
        switch (group.getProxyChooseStrategy()) {
            case RANDOM:
                return proxies.get(new Random().nextInt(proxies.size()));
            case FILL:
                return proxies.get(proxies.size() - 1);
            case BALANCE:
                return proxies.get(0);
        }
        return null;
    }

}
