package cloud.timo.TimoCloud.core;

import cloud.timo.TimoCloud.ModuleType;
import cloud.timo.TimoCloud.TimoCloudModule;
import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.core.api.TimoCloudUniversalAPICoreImplementation;
import cloud.timo.TimoCloud.core.managers.*;
import cloud.timo.TimoCloud.core.sockets.CoreSocketMessageManager;
import cloud.timo.TimoCloud.core.sockets.CoreSocketServer;
import cloud.timo.TimoCloud.core.sockets.CoreSocketServerHandler;
import cloud.timo.TimoCloud.core.sockets.CoreStringHandler;
import cloud.timo.TimoCloud.utils.options.OptionSet;
import io.netty.channel.Channel;
import org.jline.builtins.Completers;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class TimoCloudCore implements TimoCloudModule {

    private static TimoCloudCore instance;
    private boolean shuttingDown;
    private OptionSet options;
    private CoreFileManager fileManager;
    SimpleFormatter simpleFormatter = new SimpleFormatter();
    Logger logger;
    private CoreSocketServer socketServer;
    private CoreSocketServerHandler socketServerHandler;
    private CoreStringHandler stringHandler;
    private CoreServerManager serverManager;
    private Channel channel;
    private TemplateManager templateManager;
    private CommandManager commandManager;
    private CoreEventManager eventManager;
    private CoreSocketMessageManager socketMessageManager;
    private boolean running;
    private boolean waitingForCommand = false;
    private LineReader reader;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public void info(String message) {
        if (getReader() == null) {
            System.out.println(message);
            return;
        }
        if (isWaitingForCommand()) getReader().callWidget(LineReader.CLEAR);
        getReader().getTerminal().writer().print(getSimpleFormatter().format(new LogRecord(Level.INFO, message)));
        if (isWaitingForCommand()) getReader().callWidget(LineReader.REDRAW_LINE);
        if (isWaitingForCommand()) getReader().callWidget(LineReader.REDISPLAY);
        getReader().getTerminal().writer().flush();

        if (getLogger() != null) getLogger().info(message);
    }

    public void severe(String message) {
        if (getReader() == null) {
            System.err.println(message);
            return;
        }
        if (isWaitingForCommand()) getReader().callWidget(LineReader.CLEAR);
        getReader().getTerminal().writer().print(getSimpleFormatter().format(new LogRecord(Level.SEVERE, ANSI_RED + message + ANSI_RESET)));
        if (isWaitingForCommand()) getReader().callWidget(LineReader.REDRAW_LINE);
        if (isWaitingForCommand()) getReader().callWidget(LineReader.REDISPLAY);
        getReader().getTerminal().writer().flush();

        if (getLogger() != null) getLogger().severe(message);
    }

    @Override
    public void load(OptionSet optionSet) {
        running = true;
        shuttingDown = false;
        this.options = optionSet;
        makeInstances();
        getServerManager().init();
        new Thread(this::initSocketServer).start();
        registerTasks();
        try {
            waitForCommands();
        } catch (IOException e) {
            severe("Error while initializing terminal: ");
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {
        getServerManager().saveGroups();
        channel.close();
    }

    public void waitForCommands() throws IOException {
        TerminalBuilder builder = TerminalBuilder.builder();
        builder.encoding(Charset.defaultCharset());
        builder.system(true);
        Terminal terminal = builder.build();
        Completer completer = new Completers.TreeCompleter(Completers.TreeCompleter.node(""));
        Parser parser = new DefaultParser();
        String prompt = "> ";
        String rightPrompt = null;
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                .parser(parser)
                .build();
        this.reader = reader;
        while (running) {
            waitingForCommand = true;
            String line = null;
            try {
                line = reader.readLine(prompt, rightPrompt, (MaskingCallback) null, null);
            } catch (UserInterruptException e) {
            } catch (EndOfFileException e) {
                return;
            }
            if (line == null) continue;
            waitingForCommand = false;
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] split = line.split(" ");
            if (split.length == 0) continue;
            String command = split[0];
            String[] args = command.length() == 1 ? new String[0] : Arrays.copyOfRange(split, 1, split.length);
            getCommandManager().onCommand(command, args);
        }
    }

    private void makeInstances() {
        instance = this;
        this.fileManager = new CoreFileManager();
        try {
            createLogger();
        } catch (Exception e) {
            System.err.println("Error while creating logger. Aborting.");
            e.printStackTrace();
            System.exit(1);
        }
        fileManager.load();
        this.socketServerHandler = new CoreSocketServerHandler();
        this.socketServer = new CoreSocketServer();
        this.stringHandler = new CoreStringHandler();
        this.serverManager = new CoreServerManager();
        this.templateManager = new TemplateManager();
        this.commandManager = new CommandManager();
        this.eventManager = new CoreEventManager();
        this.socketMessageManager = new CoreSocketMessageManager();

        TimoCloudAPI.setUniversalImplementation(new TimoCloudUniversalAPICoreImplementation());
        TimoCloudAPI.setEventImplementation(new EventManager());
        TimoCloudAPI.getEventImplementation().registerListener(getEventManager());
    }

    private void createLogger() throws IOException {
        logger = Logger.getLogger("TimoCloudCore");
        logger.setUseParentHandlers(false);
        File logsDirectory = getFileManager().getLogsDirectory();
        logsDirectory.mkdirs();
        FileHandler fileHandler = new FileHandler(logsDirectory.getCanonicalPath() + "/core-%g.log");
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
    }

    private void registerTasks() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::everySecond, 1, 1, TimeUnit.SECONDS);
    }

    private void everySecond() {
        try {
            getServerManager().everySecond();
        } catch (Exception e) {
            severe("Unknown error while executing every-second task:");
            e.printStackTrace();
        }
    }

    public int getSocketPort() {
        return (Integer) getFileManager().getConfig().get("socket-port");
    }

    private void initSocketServer() {
        try {
            socketServer.init("0.0.0.0", getSocketPort());
        } catch (Exception e) {
            severe("Error while initializing socket server:");
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static TimoCloudCore getInstance() {
        return instance;
    }

    public boolean isWaitingForCommand() {
        return waitingForCommand;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public CoreFileManager getFileManager() {
        return fileManager;
    }

    public SimpleFormatter getSimpleFormatter() {
        return simpleFormatter;
    }

    public Logger getLogger() {
        return logger;
    }

    public CoreServerManager getServerManager() {
        return serverManager;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CoreEventManager getEventManager() {
        return eventManager;
    }

    public CoreSocketMessageManager getSocketMessageManager() {
        return socketMessageManager;
    }

    public CoreSocketServer getSocketServer() {
        return socketServer;
    }

    public CoreSocketServerHandler getSocketServerHandler() {
        return socketServerHandler;
    }

    public CoreStringHandler getStringHandler() {
        return stringHandler;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public LineReader getReader() {
        return reader;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.CORE;
    }
}
