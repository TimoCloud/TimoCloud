package cloud.timo.TimoCloud.common.utils.options;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class OptionSet {

    private final Map<String, Option> options;

    public OptionSet() {
        this(new HashMap<>());
    }

    public void add(Option option) {
        for (String name : option.getNames()) {
            options.put(name, option);
        }
    }

    public boolean has(String option) {
        return options.containsKey(option) && get(option).isSet();
    }

    public Option get(String option) {
        return options.get(option);
    }
}
