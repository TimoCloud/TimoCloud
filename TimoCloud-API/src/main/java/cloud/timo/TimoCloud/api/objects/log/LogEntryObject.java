package cloud.timo.TimoCloud.api.objects.log;

public interface LogEntryObject {

    long getTimestamp();

    LogLevel getLevel();

    String getMessage();

    /**
     * @return The message as it is printed to the console, that means i.e. <i>[2018-08-15 20:06:45] [INFORMATION] Base BASE-1 connected.</i> instead of <i>Base BASE-1 connected.</i>
     */
    String getMessageWithPrefix();

}
