package cloud.timo.TimoCloud.bukkit.signs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SignTemplate {

    private final String name;
    private final Map<String, SignLayout> layouts;
    private Set<String> sortOutStates;

    public SignTemplate(String name, Map<String, SignLayout> layouts, Collection<String> sortOutStates) {
        this.name = name;
        this.layouts = layouts;
        if (sortOutStates != null) {
            this.sortOutStates = new HashSet<>(sortOutStates);
        }
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

    /**
     * @return If custom sortOutStates for this template are defined, they will be returned. Otherwise null - if so, the server group's sortOut states will be used.
     */
    public Set<String> getSortOutStates() {
        return sortOutStates;
    }
}
