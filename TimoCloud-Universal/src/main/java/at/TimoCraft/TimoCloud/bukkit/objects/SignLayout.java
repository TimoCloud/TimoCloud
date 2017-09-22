package at.TimoCraft.TimoCloud.bukkit.objects;

import java.util.List;

public class SignLayout {

    private List<String>[] lines;
    private long updateSpeed;

    public SignLayout() {}

    public SignLayout(List<String>[] lines, long updateSpeed) {
        this.lines = lines;
        this.updateSpeed = updateSpeed;
    }

    public List<String> getLine(int lineNumber) {
        return lines[lineNumber];
    }

    public List<String>[] getLines() {
        return lines;
    }

    public long getUpdateSpeed() {
        return updateSpeed;
    }
}
