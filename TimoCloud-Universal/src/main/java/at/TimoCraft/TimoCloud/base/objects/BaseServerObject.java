package at.TimoCraft.TimoCloud.base.objects;

/**
 * Created by Timo on 16.05.17.
 */
public class BaseServerObject {
    private String name;
    private int port;
    private int ram;
    private boolean isStatic;
    private String group;
    private String token;

    public BaseServerObject(String name, int port, int ram, boolean isStatic, String group, String token) {
        this.name = name;
        this.port = port;
        this.ram = ram;
        this.isStatic = isStatic;
        this.group = group;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
