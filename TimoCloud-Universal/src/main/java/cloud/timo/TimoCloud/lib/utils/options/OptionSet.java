package cloud.timo.TimoCloud.lib.utils.options;

import java.util.HashMap;
import java.util.Map;

public class OptionSet {

    private Map<String, Option> options;

    public OptionSet() {
        options = new HashMap<>();
    }

    public OptionSet(Map<String, Option> options) {
        this.options = options;
    }

    public void add(Option option) {
        for (String name : option.getNames()) {
            options.put(name, option);
        }
    }

    public boolean has(String option) {
        return option.contains(option) && get(option).isSet();
    }

    public Option get(String option) {
        return options.get(option);
    }
}
