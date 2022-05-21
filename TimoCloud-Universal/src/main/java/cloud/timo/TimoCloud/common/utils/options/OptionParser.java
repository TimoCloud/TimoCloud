package cloud.timo.TimoCloud.common.utils.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionParser {

    private final List<OptionTemplate> optionTemplates;

    public OptionParser() {
        optionTemplates = new ArrayList<>();
    }

    public void addTemplate(String... keys) {
        optionTemplates.add(new OptionTemplate(keys));
    }

    public OptionSet parse(String... options) {
        Map<String, Option> optionSet = new HashMap<>();
        for (OptionTemplate template : optionTemplates) {
            Option option = new Option(template);
            for (String name : option.getNames()) {
                optionSet.put(name, option);
            }
        }

        for (String optionString : options) {
            if (!optionString.startsWith("-")) continue;
            while (optionString.startsWith("-")) optionString = optionString.substring(1);
            if (optionString.isEmpty()) continue;
            String name = optionString.contains("=") ? optionString.split("=")[0] : optionString;
            Option option = optionSet.get(name);
            if (option == null) continue;
            if (optionString.contains("=")) {
                if (optionString.split("=").length != 2) continue;
                option.setValue(optionString.split("=")[1]);
            } else {
                option.setSet(true);
            }
            for (String name1 : option.getNames()) {
                optionSet.put(name1, option);
            }
        }

        return new OptionSet(optionSet);
    }
}
