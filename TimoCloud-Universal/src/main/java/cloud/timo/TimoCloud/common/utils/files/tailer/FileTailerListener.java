package cloud.timo.TimoCloud.common.utils.files.tailer;

public abstract class FileTailerListener {

    public void readLine(String line) {
    }

    public void handleException(Exception e) {
    }

}
