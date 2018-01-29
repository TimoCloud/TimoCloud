package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.List;

public class ProxyGroupObjectBasicImplementation implements ProxyGroupObject {

    private String name;
    private List<ProxyObject> proxies;
    private int onlinePlayerCount;
    private int maxPlayerCount;
    private int maxPlayerCountPerProxy;
    private int ram;
    private boolean isStatic;


    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<ProxyObject> getProxies() {
        return null;
    }

    @Override
    public int getOnlinePlayerCount() {
        return 0;
    }

    @Override
    public int getMaxPlayerCount() {
        return 0;
    }

    @Override
    public int getMaxPlayerCountPerProxy() {
        return 0;
    }

    @Override
    public int getRam() {
        return 0;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public String getBase() {
        return null;
    }
}
