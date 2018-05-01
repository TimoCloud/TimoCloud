package cloud.timo.TimoCloud.core.objects;

public interface Group {
    String getName();
    GroupType getType();
    int getRam();
    int getPriority();
    boolean isStatic();
    String getBaseName();
}
