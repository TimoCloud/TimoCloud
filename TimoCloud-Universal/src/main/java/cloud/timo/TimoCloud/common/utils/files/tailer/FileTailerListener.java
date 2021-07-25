package cloud.timo.TimoCloud.common.utils.files.tailer;

public abstract class FileTailerListener {

    /**
     * Supposed to be overridden
     *
     * @param line Line
     */
    public void readLine(String line) {
    }

    /**
     * Supposed to be overridden
     *
     * @param e Exception
     */
    public void handleException(Exception e) {
    }
}
