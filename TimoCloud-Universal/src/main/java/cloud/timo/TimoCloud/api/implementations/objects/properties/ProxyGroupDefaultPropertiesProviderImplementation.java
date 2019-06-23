package cloud.timo.TimoCloud.api.implementations.objects.properties;

import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.common.utils.RandomIdGenerator;

import java.util.Collection;
import java.util.Collections;

public class ProxyGroupDefaultPropertiesProviderImplementation implements ProxyGroupProperties.ProxyGroupDefaultPropertiesProvider {

    @Override
    public Integer getMaxPlayerCountPerProxy() {
        return 500;
    }

    @Override
    public Integer getMaxPlayerCount() {
        return 100;
    }

    @Override
    public Integer getKeepFreeSlots() {
        return 100;
    }

    @Override
    public Integer getMinAmount() {
        return 0;
    }

    @Override
    public Integer getMaxAmount() {
        return -1;
    }

    @Override
    public Integer getRam() {
        return 1024;
    }

    @Override
    public String getMotd() {
        return "&6This is a &bTimo&7Cloud &6Proxy\n&aChange this MOTD in your config or per command";
    }

    @Override
    public Boolean isStatic() {
        return false;
    }

    @Override
    public Integer getPriority() {
        return 1;
    }

    @Override
    public Collection<String> getServerGroups() {
        return Collections.singleton("*");
    }

    @Override
    public String getBaseIdentifier() {
        return null;
    }

    @Override
    public ProxyChooseStrategy getProxyChooseStrategy() {
        return ProxyChooseStrategy.BALANCE;
    }

    @Override
    public Collection<String> getHostNames() {
        return Collections.emptySet();
    }

    @Override
    public String generateId() {
        return RandomIdGenerator.generateId();
    }
}
