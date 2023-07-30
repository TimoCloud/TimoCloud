package cloud.timo.TimoCloud.cord.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.cord.TimoCloudCord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProxyManager {

    public List<ProxyGroupObject> getProxyGroupByHostName(String hostName) {
        List<ProxyGroupObject> proxies = new ArrayList<>();
        for (ProxyGroupObject group : TimoCloudAPI.getUniversalAPI().getProxyGroups())
            for (String hostName1 : group.getHostNames())
                if (matches(hostName, hostName1)) proxies.add(group);
        return proxies;
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


    public ProxyObject getFreeProxy(List<ProxyGroupObject> groups) {
        for (ProxyGroupObject proxyGroupObject : groups) {
            if (proxyGroupObject.getProxyChooseStrategy() == null) {
                TimoCloudCord.getInstance().severe("Error while choosing proxy: ProxyChooseStrategy of group '" + proxyGroupObject.getName() + "' is null. Please report this.");
                return null;
            }
            List<ProxyObject> proxies = proxyGroupObject.getProxies().stream().filter(proxy -> proxy.getOnlinePlayerCount() < proxy.getGroup().getMaxPlayerCountPerProxy()).sorted(Comparator.comparing(ProxyObject::getOnlinePlayerCount)).collect(Collectors.toList());
            if (proxies.isEmpty()) continue;
            switch (proxyGroupObject.getProxyChooseStrategy()) {
                case RANDOM:
                    return proxies.get(new Random().nextInt(proxies.size()));
                case FILL:
                    return proxies.get(proxies.size() - 1);
                case BALANCE:
                    return proxies.get(0);
            }
        }

        return null;
    }

}
