package cloud.timo.TimoCloud.base.managers;


import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class BaseResourceManager {

    private OperatingSystemMXBean operatingSystemMXBean;

    public BaseResourceManager() {
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public long getFreeMemory() {
        return getOperatingSystemMXBean().getFreePhysicalMemorySize() / (1024*1024); // Convert to megabytes
    }

    public double getCpuUsage() {
        return getOperatingSystemMXBean().getSystemCpuLoad()*100; // Convert to 0-100 percentage
    }

    private OperatingSystemMXBean getOperatingSystemMXBean() {
        return operatingSystemMXBean;
    }
}
