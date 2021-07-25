package cloud.timo.TimoCloud.core.objects;

public interface Instance extends PublicKeyIdentifiable {

    Group getGroup();

    void start();

    void stop();

    void register();

    void unregister();
}
