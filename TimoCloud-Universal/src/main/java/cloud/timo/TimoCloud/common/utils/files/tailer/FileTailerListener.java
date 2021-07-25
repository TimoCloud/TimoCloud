package cloud.timo.TimoCloud.common.utils.files.tailer;

public abstract class FileTailerListener {

    public void readLine(String line) {
        // Supposed to be overridden
    }

    public void handleException(Exception e) {
        // Supposed to be overridden
    }
}
