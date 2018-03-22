package cloud.timo.TimoCloud.base.managers;


import com.sun.management.OperatingSystemMXBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;

public class BaseResourceManager {

    private OperatingSystemMXBean operatingSystemMXBean;

    public BaseResourceManager() {
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public long getFreeMemory() {
        return (getOperatingSystemMXBean().getFreePhysicalMemorySize())/(1024*1024) + getCache();
    }

    public double getCpuUsage() {
        return getOperatingSystemMXBean().getSystemCpuLoad()*100; // Convert to 0-100 percentage
    }

    private long getCache() {
        File meminfo = new File("/proc/meminfo");
        if (! meminfo.exists()) return 0;
        try {
            FileReader fileReader = new FileReader(meminfo);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while (! (line = reader.readLine()).startsWith("Cached:"));
            fileReader.close();
            reader.close();
            return Long.parseLong(line.replace("Cached:", "").replace("kB", "").trim()) / 1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private OperatingSystemMXBean getOperatingSystemMXBean() {
        return operatingSystemMXBean;
    }
}
