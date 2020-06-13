package cloud.timo.TimoCloud.api.objects.properties;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;

import java.net.InetAddress;
import java.security.PublicKey;

public class BaseProperties {

    private String id;
    private String name;
    private PublicKey publicKey;
    private Integer maxRam;
    private Integer keepFreeRam;
    private Double maxCpuLoad;

    private BaseProperties() {
        this.maxRam = getDefaultPropertiesProvider().getMaxRam();
        this.keepFreeRam = getDefaultPropertiesProvider().getKeepFreeRam();
        this.maxCpuLoad = getDefaultPropertiesProvider().getMaxCpuLoad();
    }

    public BaseProperties(String id, String name, PublicKey publicKey) {
        this();
        this.id = id;
        this.name = name;
        this.publicKey = publicKey;
    }

    public String getId() {
        return id;
    }

    public BaseProperties setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BaseProperties setName(String name) {
        this.name = name;
        return this;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public BaseProperties setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public Integer getMaxRam() {
        return maxRam;
    }

    public BaseProperties setMaxRam(Integer maxRam) {
        this.maxRam = maxRam;
        return this;
    }

    public Integer getKeepFreeRam() {
        return keepFreeRam;
    }

    public BaseProperties setKeepFreeRam(Integer keepFreeRam) {
        this.keepFreeRam = keepFreeRam;
        return this;
    }

    public Double getMaxCpuLoad() {
        return maxCpuLoad;
    }

    public BaseProperties setMaxCpuLoad(Double maxCpuLoad) {
        this.maxCpuLoad = maxCpuLoad;
        return this;
    }

    private static BaseDefaultPropertiesProvider getDefaultPropertiesProvider() {
        return TimoCloudInternalAPI.getImplementationAPI().getBaseDefaultPropertiesProvider();
    }

    public interface BaseDefaultPropertiesProvider {

        Integer getMaxRam();

        Integer getKeepFreeRam();

        Double getMaxCpuLoad();

    }

}
