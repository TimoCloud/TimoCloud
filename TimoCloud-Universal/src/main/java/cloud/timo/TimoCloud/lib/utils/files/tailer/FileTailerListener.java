package cloud.timo.TimoCloud.lib.utils.files.tailer;

public abstract class FileTailerListener {

    public void readLine(String line) {}

    public void handleException(Exception e) {}

}
