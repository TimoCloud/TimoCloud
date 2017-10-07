package at.TimoCraft.TimoCloud.bukkit.objects;

import java.util.List;
import java.util.Map;

public class SignTemplate {
    private String name;
    private Map<String, SignLayout> layouts;
    private List<String> sortOutStates;

    public SignTemplate(String name, Map<String, SignLayout> layouts) {
        this.name = name;
        this.layouts = layouts;
    }

    public String getName() {
        return name;
    }

    public SignLayout getLayout(String state) {
        return layouts.containsKey(state) ? layouts.get(state) : layouts.get("Default");
    }

    public void addLayout(String name, SignLayout layout) {
        layouts.put(name, layout);
    }

}
