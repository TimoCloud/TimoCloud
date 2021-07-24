package cloud.timo.TimoCloud.common.utils.options;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Option {

    @Getter
    private final String[] names;
    @Getter
    @Setter
    private boolean set;
    @Getter
    private String value;

    public Option(OptionTemplate template) {
        this(template.getNames());
    }

    public void setValue(String value) {
        if (value != null) this.set = true;
        this.value = value;
    }
}
