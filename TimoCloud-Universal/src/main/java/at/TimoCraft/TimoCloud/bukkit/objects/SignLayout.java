package at.TimoCraft.TimoCloud.bukkit.objects;

public class SignLayout {

    private String name;
    private String[] lines;

    public SignLayout(String name, String[] lines) {
        this.name = name;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public String[] getLines() {
        return lines;
    }
}
