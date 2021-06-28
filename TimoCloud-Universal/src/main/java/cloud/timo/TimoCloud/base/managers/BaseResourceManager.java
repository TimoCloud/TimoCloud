package cloud.timo.TimoCloud.base.managers;


import cloud.timo.TimoCloud.base.TimoCloudBase;
import com.sun.management.OperatingSystemMXBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;

public class BaseResourceManager {

    private OperatingSystemMXBean operatingSystemMXBean;
    private double lastCpuLoad;

    public BaseResourceManager() {
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.lastCpuLoad = 0.0;
    }

    public long getFreeMemory() {
        return (getOperatingSystemMXBean().getFreePhysicalMemorySize()) / (1024 * 1024) + getCache();
    }

    public double getCpuUsage() {
        double cpuLoad = getOperatingSystemMXBean().getSystemCpuLoad() * 100;
        if (Double.isNaN(cpuLoad) || cpuLoad < 0) cpuLoad = lastCpuLoad;
        return this.lastCpuLoad = cpuLoad;
    }

    private long getCache() {
        File meminfo = new File("/proc/meminfo");
        if (!meminfo.exists()) return 0;
        try {
            FileReader fileReader = new FileReader(meminfo);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while (!(line = reader.readLine()).startsWith("Cached:")) ;
            fileReader.close();
            reader.close();
            return Long.parseLong(line.replace("Cached:", "").replace("kB", "").trim()) / 1024;
        } catch (Exception e) {
            TimoCloudBase.getInstance().severe(e);
            return 0;
        }
    }

    private OperatingSystemMXBean getOperatingSystemMXBean() {
        return operatingSystemMXBean;
    }
}
