package cloud.timo.TimoCloud.common.utils.files.tailer;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileTailer implements Runnable {

    private final File file;
    private final FileTailerListener listener;
    private final long interval;
    private volatile boolean running;

    public FileTailer(File file, FileTailerListener listener, long interval) {
        this.file = file;
        this.listener = listener;
        this.interval = interval;
    }

    @Override
    public void run() {
        running = true;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            listener.handleException(e);
            return;
        }
        String line;
        while (running) {
            try {
                try {
                    line = reader.readLine();
                } catch (Exception e) {
                    listener.handleException(e);
                    continue;
                }
                if (line == null) {
                    try {
                        Thread.sleep(interval);
                    } catch (Exception ignored) {
                    }
                    continue;
                }
                listener.readLine(line);
            } catch (Exception e) {
                TimoCloudLogger.getLogger().severe("Error while processing read line: ");
                TimoCloudLogger.getLogger().severe(e);
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
