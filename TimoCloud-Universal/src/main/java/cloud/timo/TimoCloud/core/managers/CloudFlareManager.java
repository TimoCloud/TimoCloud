package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.ProxyRegisterEvent;
import cloud.timo.TimoCloud.api.events.ProxyUnregisterEvent;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.cloudflare.CloudFlareException;
import cloud.timo.TimoCloud.core.cloudflare.DnsRecord;
import cloud.timo.TimoCloud.core.cloudflare.DnsZone;
import cloud.timo.TimoCloud.core.cloudflare.SrvRecord;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.lib.objects.HttpRequestProperty;
import cloud.timo.TimoCloud.lib.utils.ArrayUtil;
import cloud.timo.TimoCloud.lib.utils.network.HttpRequestUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CloudFlareManager implements Listener {

    private static final String CLOUDFLARE_API_URL = "https://api.cloudflare.com/client/v4/";
    private ExecutorService executorService;

    public CloudFlareManager() {
        executorService = Executors.newCachedThreadPool();
        load();
    }

    public void load() {
        if (!enabled()) return;
        deleteExistingRecords();
    }

    public void unload() {
        if (!enabled()) return;
    }

    @EventHandler
    public void onProxyRegisterEvent(ProxyRegisterEvent event) {
        if (!enabled()) return;
        executorService.submit(() -> {
            Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByProxyObject(event.getProxy());
            for (String hostName : getActiveHostnames()) {
                for (String hostName1 : proxy.getGroup().getHostNames()) {
                    if (!nameMatches(hostName, hostName1)) continue;
                    proxy.setDnsRecord(addRecord(new SrvRecord(
                            null,
                            "SRV",
                            hostName,
                            formatInetAddress(proxy.getAddress().getAddress()),
                            1,
                            getZoneByName(getDomainByHostname(hostName)),
                            1,
                            1,
                            proxy.getPort(),
                            proxy.getBase().getName().toLowerCase() + ".base." + getDomainByHostname(hostName))
                    ));
                    break;
                }
            }
        });
    }

    @EventHandler
    public void onProxyUnregisterEvent(ProxyUnregisterEvent event) {
        if (!enabled()) return;
        executorService.submit(() -> {
            Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByProxyObject(event.getProxy());
            if (proxy.getDnsRecord() == null) return;
            deleteRecord(proxy.getDnsRecord());
        });
    }

    public void onBaseRegisterEvent(Base base) {
        if (!enabled()) return;
        executorService.submit(() -> {
            for (DnsZone zone : getZones()) {
                addRecord(new DnsRecord(null, "A", base.getName().toLowerCase() + ".base." + zone.getName(), formatInetAddress(base.getPublicAddress()), 1, zone));
            }
        });
    }

    public void onBaseUnregisterEvent(Base base) {
        if (!enabled()) return;
        executorService.submit(() -> {
            for (DnsZone zone : getZones()) {
                for (DnsRecord record : getRecords(zone)) {
                    if (record.getName().startsWith(base.getName().toLowerCase() + ".base.")) {
                        deleteRecord(record);
                    }
                }
            }
        });
    }

    private void deleteExistingRecords() {
        executorService.submit(() -> {
            for (DnsZone zone : getZones()) {
                for (DnsRecord record : getRecords(zone)) {
                    if (record.getName().contains(".base.")) deleteRecord(record);
                }
            }
        });
        executorService.submit(() -> {
            for (String hostname : getActiveHostnames()) { // Delete SRV records
                for (DnsRecord record : getMatchingSrvRecords(hostname, "SRV")) {
                    deleteRecord(record);
                }
            }
        });
    }

    private DnsRecord addRecord(DnsRecord record) {
        try {
            return DnsRecord.fromJson((JSONObject) request(CLOUDFLARE_API_URL + "zones/" + record.getZone().getId() + "/dns_records", "POST", record.toJson().toString()));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe(e);
            return null;
        }
    }

    private void deleteRecord(DnsRecord record) {
        try {
            request(CLOUDFLARE_API_URL + "zones/" + record.getZone().getId() + "/dns_records/" + record.getId(), "DELETE");
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe(e);
        }
    }

    private List<String> getActiveHostnames() {
        return (List<String>) TimoCloudCore.getInstance().getFileManager().getCloudFlareConfig().get("hostnames");
    }

    private List<DnsRecord> getMatchingSrvRecords(String hostName, String type) {
        String domain = getDomainByHostname(hostName.trim().toLowerCase());
        DnsZone zone = getZoneByName(domain);
        if (zone == null) return new ArrayList<>();
        return getRecords(zone).stream().filter(record -> record.getType().equals(type)).filter(record -> nameMatches(record.getName(), "_minecraft._tcp." + hostName)).collect(Collectors.toList()); // We found one valid zone, so there won't be another one
    }

    private DnsZone getZoneByName(String name) {
        for (DnsZone zone : getZones()) {
            if (zone.getName().equalsIgnoreCase(name)) return zone;
        }
        return null;
    }

    private List<DnsZone> getZones() {
        try {
            JSONArray jsons = (JSONArray) request(CLOUDFLARE_API_URL + "zones?per_page=100", "GET");
            return (List<DnsZone>) jsons.stream()
                    .map(object -> DnsZone.fromJson((JSONObject) object))
                    .filter(object -> {
                        for (String hostName : getActiveHostnames())
                            if (hostName.toLowerCase().contains(((DnsZone) object).getName().trim().toLowerCase()))
                                return true;
                        return false;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while getting DNS zones via API. Probably your API access data is invalid.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<DnsRecord> getRecords(DnsZone zone) {
        try {
            JSONArray jsons = (JSONArray) request(CLOUDFLARE_API_URL + "zones/" + zone.getId() + "/dns_records?per_page=100", "GET");
            return (List<DnsRecord>) jsons.stream().map(object -> DnsRecord.fromJson((JSONObject) object)).collect(Collectors.toList());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while getting DNS records via API. Probably your API access data is invalid.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean enabled() {
        return (Boolean) TimoCloudCore.getInstance().getFileManager().getCloudFlareConfig().get("enabled");
    }

    private Object request(String url, String method, String data, HttpRequestProperty... additionalProperties) throws CloudFlareException {
        try {
            JSONObject response = (JSONObject) HttpRequestUtil.requestJson(url, method, data, ArrayUtil.concatArrays(getRequestProperties(), additionalProperties));
            if (!(Boolean) response.get("success")) {
                throw new CloudFlareException("CloudFlare API returned an error: " + response.get("errors"));
            }
            return response.getOrDefault("result", null);
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while executing request '" + url + "': ");
            TimoCloudCore.getInstance().severe(e);
            return null;
        }
    }

    private Object request(String url, String method, HttpRequestProperty... additionalProperties) throws CloudFlareException {
        return request(url, method, (String) null, additionalProperties);
    }

    private static String getDomainByHostname(String hostName) {
        String[] hostNameSplit = hostName.split("\\.");
        return hostNameSplit.length <= 1 ? hostName : hostNameSplit[hostNameSplit.length - 2] + "." + hostNameSplit[hostNameSplit.length - 1];
    }

    private static boolean nameMatches(String a, String b) {
        if (a.equals("*") || b.equals("*")) return true;
        String[] as = a.trim().toLowerCase().split("\\.");
        String[] bs = b.trim().toLowerCase().split("\\.");
        int i = as.length, j = bs.length;
        while (i > 0 && j > 0) {
            i--;
            j--;
            String ac = as[i];
            String bc = bs[j];
            if ((i == 0 && ac.equals("*")) || (j == 0 && bc.equals("*"))) return true;
            if (!ac.equals(bc)) return false;
        }
        return i == j;
    }

    private HttpRequestProperty[] getRequestProperties() {
        return new HttpRequestProperty[]{
                new HttpRequestProperty("X-Auth-Email", (String) TimoCloudCore.getInstance().getFileManager().getCloudFlareConfig().get("email")),
                new HttpRequestProperty("X-Auth-Key", (String) TimoCloudCore.getInstance().getFileManager().getCloudFlareConfig().get("api-key")),
                new HttpRequestProperty("Content-Type", "application/json"),
                new HttpRequestProperty("Accept", "application/json")
        };
    }

    private static String formatInetAddress(InetAddress address) {
        if (address.toString().startsWith("/")) return address.toString().substring(1);
        return address.toString();
    }
}
