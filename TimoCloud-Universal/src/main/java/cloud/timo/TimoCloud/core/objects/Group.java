package cloud.timo.TimoCloud.core.objects;

public interface Group extends Identifiable {

    String getName();

    GroupType getType();

    int getRam();

    int getPriority();

    boolean isStatic();

    Base getBase();

}
