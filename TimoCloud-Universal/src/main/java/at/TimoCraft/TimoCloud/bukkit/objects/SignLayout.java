package at.TimoCraft.TimoCloud.bukkit.objects;

import org.bukkit.Material;

import java.util.List;

public class SignLayout {

    private List<String>[] lines;
    private long updateSpeed;
    private Material signBlockMaterial;
    private int signBlockData;

    public SignLayout() {}

    public SignLayout(List<String>[] lines, long updateSpeed, Material signBlockMaterial, int signBlockData) {
        this.lines = lines;
        this.updateSpeed = updateSpeed;
        this.signBlockMaterial = signBlockMaterial;
        this.signBlockData = signBlockData;
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

    public Material getSignBlockMaterial() {
        return signBlockMaterial;
    }

    public int getSignBlockData() {
        return signBlockData;
    }
}
