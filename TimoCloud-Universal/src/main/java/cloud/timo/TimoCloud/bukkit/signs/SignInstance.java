package cloud.timo.TimoCloud.bukkit.signs;

import cloud.timo.TimoCloud.api.objects.ServerObject;
import org.bukkit.Location;

public class SignInstance {

    private Location location;
    private String target;
    private String templateName;
    private SignTemplate template;
    private boolean dynamic;
    private int priority;
    private int step = 0;
    private ServerObject targetServer;
    private boolean active = true;

    public SignInstance(Location location, String target, String templateName, SignTemplate template, boolean dynamic, int priority) {
        this.location = location;
        this.target = target;
        this.templateName = templateName;
        this.template = template;
        this.dynamic = dynamic;
        this.priority = priority;
    }

    public Location getLocation() {
        return location;
    }

    public String getTarget() {
        return target;
    }

    public String getTemplateName() {
        return templateName;
    }

    public SignTemplate getTemplate() {
        return template;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public int getPriority() {
        return priority;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public ServerObject getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(ServerObject targetServer) {
        this.targetServer = targetServer;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
