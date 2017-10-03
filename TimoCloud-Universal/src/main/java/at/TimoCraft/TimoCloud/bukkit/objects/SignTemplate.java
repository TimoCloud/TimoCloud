package at.TimoCraft.TimoCloud.bukkit.objects;

import java.util.Map;
import java.util.List;

public class SignTemplate {
    private String name;
    private Map<String, SignLayout> layouts;
    private List<String> sortOutStates;

    public SignTemplate(String name, Map<String, SignLayout> layouts, List<String> sortOut) {
        this.name = name;
        this.layouts = layouts;
        this.sortOutStates = sortOut;
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

    public List<String> getSortOutStates() {
        return sortOutStates;
    }

    public boolean isSortedOut(String state) {
        return getSortOutStates().contains(state);
    }
}
