package cloud.timo.TimoCloud.core;

import cloud.timo.TimoCloud.ModuleType;
import cloud.timo.TimoCloud.TimoCloudModule;
import cloud.timo.TimoCloud.core.managers.CoreFileManager;
import cloud.timo.TimoCloud.core.managers.CoreServerManager;
import cloud.timo.TimoCloud.core.managers.TemplateManager;
import cloud.timo.TimoCloud.core.sockets.CoreSocketServer;
import cloud.timo.TimoCloud.core.sockets.CoreSocketServerHandler;
import cloud.timo.TimoCloud.utils.options.OptionSet;


import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TimoCloudCore implements TimoCloudModule {

    private static TimoCloudCore instance;
    private boolean shuttingDown;
    private OptionSet options;
    private CoreFileManager fileManager;
    Logger logger;
    private CoreSocketServer socketServer;
    private CoreSocketServerHandler socketServerHandler;
    private CoreServerManager serverManager;
    private TemplateManager templateManager;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public void info(String message) {
        if (logger == null) {
            System.out.println(message);
            return;
        }
        logger.info(message);
    }

    public void severe(String message) {
        if (logger == null) {
            System.err.println(message);
            return;
        }
        logger.severe(message);
    }

    @Override
    public void load(OptionSet optionSet) {
        shuttingDown = false;
        this.options = optionSet;
        makeInstances();
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
        this.socketServerHandler = new CoreSocketServerHandler();
        this.socketServer = new CoreSocketServer();
        new Thread(this::initSocketServer).start();
        this.serverManager = new CoreServerManager();
        this.templateManager = new TemplateManager();
        registerTasks();
    }

    private void createLogger() throws IOException {
        logger = Logger.getLogger("TimoCloudCore");
        FileHandler fileHandler = new FileHandler(getFileManager().getLogsDirectory().getCanonicalPath() + "/core-%g.log");
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
    }

    private void registerTasks() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::everySecond, 1, 1, TimeUnit.SECONDS);
    }

    private void everySecond() {
        getServerManager().everySecond();
    }

    private void initSocketServer() {
        try {
            socketServer.init("0.0.0.0", (Integer) getFileManager().getConfig().get("socket-port"));
        } catch (Exception e) {
            severe("Error while initializing socket server:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static TimoCloudCore getInstance() {
        return instance;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public CoreFileManager getFileManager() {
        return fileManager;
    }

    public CoreServerManager getServerManager() {
        return serverManager;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    public CoreSocketServerHandler getSocketServerHandler() {
        return socketServerHandler;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.CORE;
    }
}
