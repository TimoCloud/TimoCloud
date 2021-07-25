package cloud.timo.TimoCloud.core;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.internal.TimoCloudInternalImplementationAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.APIResponseManager;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.plugins.TimoCloudPlugin;
import cloud.timo.TimoCloud.api.utils.APIInstanceUtil;
import cloud.timo.TimoCloud.common.modules.ModuleType;
import cloud.timo.TimoCloud.common.modules.TimoCloudModule;
import cloud.timo.TimoCloud.common.utils.options.OptionSet;
import cloud.timo.TimoCloud.core.api.TimoCloudCoreAPIImplementation;
import cloud.timo.TimoCloud.core.api.TimoCloudInternalMessageAPICoreImplementation;
import cloud.timo.TimoCloud.core.api.TimoCloudMessageAPICoreImplementation;
import cloud.timo.TimoCloud.core.api.TimoCloudUniversalAPICoreImplementation;
import cloud.timo.TimoCloud.core.managers.APIRequestManager;
import cloud.timo.TimoCloud.core.managers.CloudFlareManager;
import cloud.timo.TimoCloud.core.managers.CommandManager;
import cloud.timo.TimoCloud.core.managers.CoreEventManager;
import cloud.timo.TimoCloud.core.managers.CoreFileManager;
import cloud.timo.TimoCloud.core.managers.CoreInstanceManager;
import cloud.timo.TimoCloud.core.managers.CorePublicKeyManager;
import cloud.timo.TimoCloud.core.managers.PluginMessageManager;
import cloud.timo.TimoCloud.core.managers.TemplateManager;
import cloud.timo.TimoCloud.core.plugins.PluginManager;
import cloud.timo.TimoCloud.core.sockets.CoreSocketServer;
import cloud.timo.TimoCloud.core.sockets.CoreSocketServerHandler;
import cloud.timo.TimoCloud.core.sockets.CoreStringHandler;
import cloud.timo.TimoCloud.core.utils.completers.BaseNameCompleter;
import cloud.timo.TimoCloud.core.utils.completers.ProxyGroupNameCompleter;
import cloud.timo.TimoCloud.core.utils.completers.ProxyNameCompleter;
import cloud.timo.TimoCloud.core.utils.completers.ServerGroupNameCompleter;
import cloud.timo.TimoCloud.core.utils.completers.ServerNameCompleter;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class TimoCloudCore implements TimoCloudModule {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    @Getter
    private static TimoCloudCore instance;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    @Getter
    private final SimpleFormatter simpleFormatter = new SimpleFormatter();
    @Getter
    private boolean shuttingDown;
    @Getter
    private CoreFileManager fileManager;
    @Getter
    private Logger logger;
    @Getter
    private CoreSocketServer socketServer;
    @Getter
    private CoreSocketServerHandler socketServerHandler;
    @Getter
    private CoreStringHandler stringHandler;
    @Getter
    private CoreInstanceManager instanceManager;
    @Getter
    @Setter
    private Channel channel;
    @Getter
    private TemplateManager templateManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private CoreEventManager eventManager;
    @Getter
    private CloudFlareManager cloudFlareManager;
    @Getter
    private PluginManager pluginManager;
    @Getter
    private PluginMessageManager pluginMessageManager;
    @Getter
    private APIRequestManager apiRequestManager;
    @Getter
    private CorePublicKeyManager corePublicKeyManager;
    private boolean running;
    @Getter
    private boolean waitingForCommand = false;
    @Getter
    private LineReader reader;

    @Override
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

    @Override
    public void warning(String message) {
        if (getReader() == null) {
            System.out.println(message);
            return;
        }
        if (isWaitingForCommand()) getReader().callWidget(LineReader.CLEAR);
        getReader().getTerminal().writer().print(getSimpleFormatter().format(new LogRecord(Level.WARNING, message)));
        if (isWaitingForCommand()) getReader().callWidget(LineReader.REDRAW_LINE);
        if (isWaitingForCommand()) getReader().callWidget(LineReader.REDISPLAY);
        getReader().getTerminal().writer().flush();

        if (getLogger() != null) getLogger().warning(message);
    }

    @Override
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
    public void load(OptionSet optionSet) throws Exception {
        running = true;
        shuttingDown = false;
        makeInstances();
        getInstanceManager().init();
        new Thread(this::initSocketServer).start();
        registerTasks();
        getPluginManager().loadPlugins();
        try {
            waitForCommands();
        } catch (IOException e) {
            severe("Error while reading commands: ");
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {
        for (TimoCloudPlugin plugin : getPluginManager().getPlugins()) {
            plugin.onUnload();
        }
        channel.close();
    }

    public void waitForCommands() throws IOException {
        TerminalBuilder builder = TerminalBuilder.builder();
        builder.encoding(Charset.defaultCharset());
        builder.system(true);
        Terminal terminal = builder.build();
        Completer completer = new Completers.TreeCompleter(
                node("help"),
                node("version"),
                node("reload"),
                node("addgroup",
                        node("server"),
                        node("proxy")),
                node("removegroup"),
                node("editgroup", new Completers.TreeCompleter.Node(new AggregateCompleter(new ServerGroupNameCompleter(), new ProxyGroupNameCompleter()), Collections.emptyList())),
                node("restart", new Completers.TreeCompleter.Node(new AggregateCompleter(new ServerGroupNameCompleter(), new ProxyGroupNameCompleter(), new ServerNameCompleter(), new ProxyNameCompleter()), Collections.emptyList())),
                node("listgroups"),
                node("groupinfo", new Completers.TreeCompleter.Node(new AggregateCompleter(new ServerGroupNameCompleter(), new ProxyGroupNameCompleter()), Collections.emptyList())),
                node("listgroups"),
                node("baseinfo", new Completers.TreeCompleter.Node(new BaseNameCompleter(), Collections.emptyList())),
                node("listbases"),
                node("sendcommand", new Completers.TreeCompleter.Node(new AggregateCompleter(new ServerGroupNameCompleter(), new ProxyGroupNameCompleter(), new ServerNameCompleter(), new ProxyNameCompleter()), Collections.emptyList())),
                node("addbase")
        );
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
                System.exit(0);
            } catch (EndOfFileException ignore) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (line == null) continue;
            waitingForCommand = false;
            line = line.trim();
            if (line.isEmpty()) continue;
            getCommandManager().onCommand(line);
        }
    }

    private void makeInstances() throws Exception {
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
        this.instanceManager = new CoreInstanceManager();
        this.templateManager = new TemplateManager();
        this.commandManager = new CommandManager();
        this.eventManager = new CoreEventManager();
        this.cloudFlareManager = new CloudFlareManager();
        this.pluginManager = new PluginManager();
        this.pluginMessageManager = new PluginMessageManager();
        this.apiRequestManager = new APIRequestManager();
        this.corePublicKeyManager = new CorePublicKeyManager();

        APIInstanceUtil.setEventInstance(new EventManager());
        APIInstanceUtil.setUniversalInstance(new TimoCloudUniversalAPICoreImplementation());
        APIInstanceUtil.setCoreInstance(new TimoCloudCoreAPIImplementation());
        APIInstanceUtil.setMessageInstance(new TimoCloudMessageAPICoreImplementation());
        APIInstanceUtil.setInternalImplementationAPIInstance(new TimoCloudInternalImplementationAPIBasicImplementation());
        APIInstanceUtil.setInternalMessageInstance(new TimoCloudInternalMessageAPICoreImplementation());
        TimoCloudAPI.getEventAPI().registerListener(getEventManager());
        TimoCloudAPI.getEventAPI().registerListener(getCloudFlareManager());
        TimoCloudAPI.getMessageAPI().registerMessageListener(getApiRequestManager(), "TIMOCLOUD_API_REQUEST");
        TimoCloudAPI.getMessageAPI().registerMessageListener(new APIResponseManager(), "TIMOCLOUD_API_RESPONSE");
    }

    private void createLogger() throws IOException {
        logger = Logger.getLogger("TimoCloudCore");
        logger.setUseParentHandlers(false);
        File logsDirectory = getFileManager().getLogsDirectory();
        logsDirectory.mkdirs();
        FileHandler fileHandler = new FileHandler(logsDirectory.getCanonicalPath() + "/core-%g.log", 5242880, 100, false);
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
            getInstanceManager().everySecond();
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.CORE;
    }
}
