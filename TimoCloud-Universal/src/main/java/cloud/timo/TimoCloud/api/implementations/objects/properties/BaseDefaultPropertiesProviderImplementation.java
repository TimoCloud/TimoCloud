package cloud.timo.TimoCloud.api.implementations.objects.properties;

import cloud.timo.TimoCloud.api.objects.properties.BaseProperties;

public class BaseDefaultPropertiesProviderImplementation implements BaseProperties.BaseDefaultPropertiesProvider {

    @Override
    public Integer getMaxRam() {
        return 64*1024;
    }

    @Override
    public Integer getKeepFreeRam() {
        return 512;
    }

    @Override
    public Double getMaxCpuLoad() {
        return 90.0;
    }
}
