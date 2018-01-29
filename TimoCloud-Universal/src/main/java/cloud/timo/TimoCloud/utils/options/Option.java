package cloud.timo.TimoCloud.utils.options;

public class Option {
    private String[] names;
    private boolean set;
    private String value;

    public Option(String[] names) {
        this.names = names;
    }

    public Option(OptionTemplate template) {
        this(template.getNames());
    }

    public String[] getNames() {
        return names;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null) this.set = true;
        this.value = value;
    }

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }
}
