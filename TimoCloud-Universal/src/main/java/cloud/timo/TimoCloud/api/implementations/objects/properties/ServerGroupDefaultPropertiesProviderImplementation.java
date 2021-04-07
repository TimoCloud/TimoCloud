package cloud.timo.TimoCloud.api.implementations.objects.properties;

import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;
import cloud.timo.TimoCloud.common.utils.RandomIdGenerator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ServerGroupDefaultPropertiesProviderImplementation implements ServerGroupProperties.ServerGroupDefaultPropertiesProvider {

    @Override
    public Integer getOnlineAmount() {
        return 1;
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
    public Boolean isStatic() {
        return false;
    }

    @Override
    public Integer getPriority() {
        return 1;
    }

    @Override
    public String getBaseIdentifier() {
        return null;
    }

    @Override
    public Collection<String> getSortOutStates() {
        return Arrays.asList("OFFLINE", "STARTING", "INGAME", "RESTARTING");
    }

    @Override
    public String generateId() {
        return RandomIdGenerator.generateId();
    }

    @Override
    public List<String> getJavaParameters() {
        return Arrays.asList(
                "-Dfile.encoding=UTF8",
                "-XX:+UseG1GC",
                "-XX:+UnlockExperimentalVMOptions",
                "-XX:+DoEscapeAnalysis",
                "-XX:+UseCompressedOops",
                "-XX:MaxGCPauseMillis=10",
                "-XX:GCPauseIntervalMillis=100",
                "-XX:+UseAdaptiveSizePolicy",
                "-XX:ParallelGCThreads=2",
                "-XX:UseSSE=3");
    }

    @Override
    public List<String> getSpigotParameters() {
        return Arrays.asList("-o false", "-h 0.0.0.0");
    }

    @Override
    public String getJrePath() {
        return "java";
    }
}
