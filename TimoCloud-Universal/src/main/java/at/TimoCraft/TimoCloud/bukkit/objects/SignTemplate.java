package at.TimoCraft.TimoCloud.bukkit.objects;

import java.util.List;

public class SignTemplate {
    private String name;
    private List<SignLayout> layouts;
    private long updateSpeed;

    public SignTemplate(String name, List<SignLayout> layouts, long updateSpeed) {
        this.name = name;
        this.layouts = layouts;
        this.updateSpeed = updateSpeed;
    }

    public String getName() {
        return name;
    }

    public List<SignLayout> getLayouts() {
        return layouts;
    }

    public long getUpdateSpeed() {
        return updateSpeed;
    }
}
