package cloud.timo.TimoCloud.core.objects;

public interface Instance {

    String getName();
    String getId();

    Group getGroup();

    void start();
    void stop();
    void register();
    void unregister();
}
