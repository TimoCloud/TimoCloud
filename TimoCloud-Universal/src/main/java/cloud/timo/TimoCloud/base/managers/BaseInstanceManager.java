package cloud.timo.TimoCloud.base.managers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.exceptions.InstanceStartException;
import cloud.timo.TimoCloud.base.exceptions.ProxyStartException;
import cloud.timo.TimoCloud.base.exceptions.ServerStartException;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.base.utils.LogTailerListener;
import cloud.timo.TimoCloud.common.encryption.RSAKeyPairRetriever;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.log.LogEntry;
import cloud.timo.TimoCloud.common.log.LogEntryReader;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.utils.HashUtil;
import cloud.timo.TimoCloud.common.utils.files.tailer.FileTailer;
import cloud.timo.TimoCloud.cord.utils.MathUtil;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BaseInstanceManager {

    private static final long STATIC_CREATE_TIME = 1482773874000L; // This is the exact time the project TimoCloud has come to life at
    private static final Integer SERVER_PORT_START = 41000;
    private static final Integer SERVER_PORT_MAX = 42000;
    private static final Integer PROXY_PORT_START = 40000;
    private static final Integer PROXY_PORT_MAX = 40300;

    private LinkedList<BaseServerObject> serverQueue;
    private LinkedList<BaseProxyObject> proxyQueue;

    private final ScheduledExecutorService scheduler;

    private Map<String, FileTailer> logTailers;

    private boolean startingServer = false;
    private Integer currentServerPort = SERVER_PORT_START;

    private boolean startingProxy = false;
    private Integer currentProxyPort = PROXY_PORT_START;

    private boolean downloadingTemplate = false;

    public BaseInstanceManager(long millis) {
        serverQueue = new LinkedList<>();
        proxyQueue = new LinkedList<>();
        scheduler = Executors.newScheduledThreadPool(1);
        logTailers = new HashMap<>();
        scheduler.scheduleAtFixedRate(this::everySecond, millis, millis, TimeUnit.MILLISECONDS);
    }

    public void updateResources() {
        double cpu = TimoCloudBase.getInstance().getResourceManager().getCpuUsage();
        cpu = MathUtil.round(cpu, 2); // Do not send all decimal places
        boolean ready = serverQueue.isEmpty() && proxyQueue.isEmpty() && !startingServer && !startingProxy;
        long freeRam = TimoCloudBase.getInstance().getResourceManager().getFreeMemory();
        TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(
                Message.create().setType(MessageType.BASE_RESOURCES)
                        .setData(Message.create()
                                .set("ready", ready)
                                .set("freeRam", freeRam)
                                .set("cpuLoad", cpu)));
    }

    public void addToServerQueue(BaseServerObject server) {
        serverQueue.add(server);
    }

    public void addToProxyQueue(BaseProxyObject proxy) {
        proxyQueue.add(proxy);
    }

    private void everySecond() {
        try {
            startNext();
        } catch (Exception e) {
            TimoCloudBase.getInstance().severe(e);
        }
    }

    public void startNext() {
        if (isDownloadingTemplate()) return;
        startNextServer();
        startNextProxy();
        updateResources();
    }

    public void startNextServer() {
        if (serverQueue.isEmpty()) return;
        BaseServerObject server = serverQueue.pop();
        if (server == null) {
            startNextServer();
            return;
        }
        startingServer = true;
        startServer(server);
        startingServer = false;
    }

    public void startNextProxy() {
        if (proxyQueue.isEmpty()) return;
        BaseProxyObject proxy = proxyQueue.pop();
        if (proxy == null) {
            startNextProxy();
            return;
        }
        startingProxy = true;
        startProxy(proxy);
        startingProxy = false;
    }

    private void copyDirectory(File from, File to) throws IOException {
        FileUtils.copyDirectory(from, to);
    }

    private void copyDirectoryCarefully(File from, File to, long value, int layer) throws IOException {
        if (layer > 25) {
            throw new IOException("Too many layers. This could be caused by a symlink loop. File: " + to.getAbsolutePath());
        }
        for (File file : from.listFiles()) {
            File toFile = new File(to, file.getName());
            if (file.isDirectory()) {
                copyDirectoryCarefully(file, toFile, value, layer + 1);
            } else {
                if (toFile.exists() && toFile.lastModified() != value) continue;

                FileUtils.copyFileToDirectory(file, to);
                toFile.setLastModified(value);
            }
        }
    }

    private void startServer(BaseServerObject server) {
        TimoCloudBase.getInstance().info("Starting server " + server.getName() + "...");
        double millisBefore = System.currentTimeMillis();
        try {
            File templateDirectory = new File((server.isStatic() ? TimoCloudBase.getInstance().getFileManager().getServerStaticDirectory() : TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory()), server.getGroup());
            if (!templateDirectory.exists()) templateDirectory.mkdirs();

            File mapDirectory = new File(TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory(), server.getGroup() + "_" + server.getMap());

            Map<String, Object> templateHashes = server.isStatic() ? null : HashUtil.getHashes(templateDirectory);
            Map<String, Object> mapHashes = (!server.isStatic() && server.getMapHash() != null) ? HashUtil.getHashes(mapDirectory) : null;
            Map<String, Object> globalHashes = HashUtil.getHashes(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory());

            if (server.getTemplateHash() != null)
                HashUtil.deleteIfNotExisting(templateDirectory, "", templateHashes, server.getTemplateHash());
            if (server.getMapHash() != null)
                HashUtil.deleteIfNotExisting(mapDirectory, "", mapHashes, server.getMapHash());
            HashUtil.deleteIfNotExisting(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory(), "", globalHashes, server.getGlobalHash());

            templateHashes = server.isStatic() ? null : HashUtil.getHashes(templateDirectory);
            mapHashes = (!server.isStatic() && server.getMapHash() != null) ? HashUtil.getHashes(mapDirectory) : null;
            globalHashes = HashUtil.getHashes(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory());

            List<String> templateDifferences = server.isStatic() ? new ArrayList<>() : HashUtil.getDifferentFiles("", server.getTemplateHash(), templateHashes);
            List<String> mapDifferences = (!server.isStatic() && server.getMapHash() != null) ? HashUtil.getDifferentFiles("", server.getMapHash(), mapHashes) : new ArrayList<>();
            List<String> globalDifferences = HashUtil.getDifferentFiles("", server.getGlobalHash(), globalHashes);

            if (templateDifferences.size() > 0 || mapDifferences.size() > 0 || globalDifferences.size() > 0) {
                TimoCloudBase.getInstance().info("New server template updates found! Stopping and downloading updates...");
                TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create()
                        .setType(MessageType.BASE_SERVER_TEMPLATE_REQUEST)
                        .setTarget(server.getId())
                        .setIfCondition("template", templateDirectory.getName(), templateDifferences.size() > 0)
                        .setIfCondition("map", server.getMap(), mapDifferences.size() > 0)
                        .set("differences",
                                Message.create()
                                        .setIfCondition("templateDifferences", templateDifferences, templateDifferences.size() > 0)
                                        .setIfCondition("mapDifferences", mapDifferences, mapDifferences.size() > 0)
                                        .setIfCondition("globalDifferences", globalDifferences, globalDifferences.size() > 0)));
                setDownloadingTemplate(true);
                return;
            }

            File temporaryDirectory = server.isStatic() ? templateDirectory : new File(TimoCloudBase.getInstance().getFileManager().getServerTemporaryDirectory(), server.getId());
            if (!server.isStatic()) {
                if (temporaryDirectory.exists()) BaseFileManager.deleteDirectory(temporaryDirectory);
                copyDirectory(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory(), temporaryDirectory);
            }

            if (server.isStatic()) {
                copyDirectoryCarefully(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory(), temporaryDirectory, STATIC_CREATE_TIME, 1);
            } else {
                copyDirectory(templateDirectory, temporaryDirectory);
            }

            boolean randomMap = server.getMap() != null;
            String mapName = server.getMap() == null ? "Default" : server.getMap();
            if (!server.isStatic() && server.getMap() != null) {
                randomMap = true;
                if (mapDirectory.exists()) copyDirectory(mapDirectory, temporaryDirectory);
            }

            File spigotJar = new File(temporaryDirectory, "spigot.jar");
            if (!spigotJar.exists()) {
                TimoCloudBase.getInstance().severe("Could not start server " + server.getName() + " because spigot.jar does not exist. " + (
                        server.isStatic() ? "Please make sure the file " + spigotJar.getAbsolutePath() + " exists (case sensitive!)."
                                : "Please make sure to have a file called 'spigot.jar' in your template."));
                throw new ServerStartException("spigot.jar does not exist");
            }

            File plugins = new File(temporaryDirectory, "/plugins/");
            plugins.mkdirs();
            File plugin = new File(plugins, "TimoCloud.jar");
            if (plugin.exists()) plugin.delete();
            try {
                Files.copy(new File(TimoCloudBase.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath(), plugin.toPath());
            } catch (Exception e) {
                TimoCloudBase.getInstance().severe("Error while copying plugin into template:");
                TimoCloudBase.getInstance().severe(e);
                throw new ServerStartException("Could not copy TimoCloud.jar into template");
            }

            Integer serverPort = getFreePortCommon(SERVER_PORT_START, currentServerPort, SERVER_PORT_MAX);
            currentServerPort = serverPort + 1;

            PublicKey publicKey = new RSAKeyPairRetriever(new File(temporaryDirectory, "plugins/TimoCloud/keys/")).generateKeyPair().getPublic();

            File serverProperties = new File(temporaryDirectory, "server.properties");
            setProperty(serverProperties, "online-mode", "false");
            setProperty(serverProperties, "server-name", server.getName());
            File configFile = new File(temporaryDirectory, "spigot.yml");
            configFile.createNewFile();
            Yaml yaml = new Yaml();
            Map<String, Object> config = (Map<String, Object>) yaml.load(new FileReader(configFile));
            if (config == null) config = new LinkedHashMap<>();
            Map<String, Object> settings = (Map<String, Object>) config.get("settings");
            if (settings == null) settings = new LinkedHashMap<>();
            final Boolean bungeeCordMode = (Boolean) settings.getOrDefault("bungeecord", false);

            if (!bungeeCordMode) {
                TimoCloudBase.getInstance().warning(server.getName() + " is not in BungeeCord mode \n" +
                        "To fix this, change bungeecord to true in spigot.yml");
            }

            double millisNow = System.currentTimeMillis();
            TimoCloudBase.getInstance().info("Successfully prepared starting server " + server.getName() + " in " + (millisNow - millisBefore) / 1000 + " seconds.");

            File logFile = getServerLogFile(server.getId());
            logFile.createNewFile();

            FileTailer logTailer = generateLogTailer(logFile, (logEntry) -> {
                TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create()
                        .setType(MessageType.SERVER_LOG_ENTRY)
                        .setData(logEntry)
                        .setTarget(server.getId()));
            });
            new Thread(logTailer).start();
            this.logTailers.put(server.getId(), logTailer);

            try {
                String logString = "";
                if (getScreenVersion() >= 40602) {
                    logString = " -L -Logfile " + logFile.getAbsolutePath();
                }

                Process process = new ProcessBuilder(
                        "/bin/sh", "-c",
                        "screen -mdS " + server.getId() +
                                logString +
                                " /bin/sh -c '" +
                                "cd " + temporaryDirectory.getAbsolutePath() + " &&" +
                                " " + server.getJrePath() + " -server" +
                                " -Xmx" + server.getRam() + "M " +
                                buildStartParameters(server.getJavaParameters()) +
                                " -Dcom.mojang.eula.agree=true" +
                                " -Dtimocloud-servername=" + server.getName() +
                                " -Dtimocloud-serverid=" + server.getId() +
                                " -Dtimocloud-corehost=" + TimoCloudBase.getInstance().getCoreSocketIP() + ":" + TimoCloudBase.getInstance().getCoreSocketPort() +
                                " -Dtimocloud-randommap=" + randomMap +
                                " -Dtimocloud-mapname=" + mapName +
                                " -Dtimocloud-static=" + server.isStatic() +
                                " -Dtimocloud-templatedirectory=" + templateDirectory.getAbsolutePath() +
                                " -Dtimocloud-temporarydirectory=" + temporaryDirectory.getAbsolutePath() +
                                " -jar spigot.jar -p " + serverPort + " " + buildStartParameters(server.getSpigotParameters()) +
                                "'"
                ).start();
                TimoCloudBase.getInstance().info("Successfully started server screen session " + server.getName() + ".");
            } catch (Exception e) {
                TimoCloudBase.getInstance().severe("Error while starting server " + server.getName() + ":");
                TimoCloudBase.getInstance().severe(e);
                throw new ServerStartException("Could not start process");
            }

            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create()
                    .setType(MessageType.BASE_SERVER_STARTED)
                    .setTarget(server.getId())
                    .set("port", serverPort)
                    .set("publicKey", RSAKeyUtil.publicKeyToBase64(publicKey))
            );

        } catch (Exception e) {
            TimoCloudBase.getInstance().severe("Error while starting server " + server.getName() + ": " + e.getMessage());
            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.BASE_SERVER_NOT_STARTED).setTarget(server.getId()));
        }
    }


    private void startProxy(BaseProxyObject proxy) {
        TimoCloudBase.getInstance().info("Starting proxy " + proxy.getName() + "...");
        double millisBefore = System.currentTimeMillis();
        try {
            File templateDirectory = new File((proxy.isStatic() ? TimoCloudBase.getInstance().getFileManager().getProxyStaticDirectory() : TimoCloudBase.getInstance().getFileManager().getProxyTemplatesDirectory()), proxy.getGroup());
            if (!templateDirectory.exists()) templateDirectory.mkdirs();

            Map<String, Object> templateHashes = proxy.isStatic() ? null : HashUtil.getHashes(templateDirectory);
            Map<String, Object> globalHashes = HashUtil.getHashes(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory());

            if (proxy.getTemplateHash() != null)
                HashUtil.deleteIfNotExisting(templateDirectory, "", templateHashes, proxy.getTemplateHash());
            HashUtil.deleteIfNotExisting(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory(), "", globalHashes, proxy.getGlobalHash());

            templateHashes = HashUtil.getHashes(templateDirectory);
            globalHashes = HashUtil.getHashes(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory());

            List<String> templateDifferences = proxy.isStatic() ? new ArrayList<>() : HashUtil.getDifferentFiles("", proxy.getTemplateHash(), templateHashes);
            List<String> gloalDifferences = HashUtil.getDifferentFiles("", proxy.getGlobalHash(), globalHashes);

            if (templateDifferences.size() > 0 || gloalDifferences.size() > 0) {
                TimoCloudBase.getInstance().info("New proxy template updates found! Stopping and downloading updates...");
                TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(
                        Message.create()
                                .setType(MessageType.BASE_PROXY_TEMPLATE_REQUEST)
                                .setTarget(proxy.getId())
                                .setIfCondition("template", templateDirectory.getName(), templateDifferences.size() > 0)
                                .set("differences", Message.create()
                                        .setIfCondition("templateDifferences", templateDifferences, templateDifferences.size() > 0)
                                        .setIfCondition("globalDifferences", gloalDifferences, gloalDifferences.size() > 0)));
                setDownloadingTemplate(true);
                return;
            }

            File temporaryDirectory = proxy.isStatic() ? templateDirectory : new File(TimoCloudBase.getInstance().getFileManager().getProxyTemporaryDirectory(), proxy.getId());
            if (!proxy.isStatic()) {
                if (temporaryDirectory.exists()) BaseFileManager.deleteDirectory(temporaryDirectory);
                copyDirectory(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory(), temporaryDirectory);
            }

            if (proxy.isStatic()) {
                copyDirectoryCarefully(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory(), temporaryDirectory, STATIC_CREATE_TIME, 1);
            } else {
                copyDirectory(templateDirectory, temporaryDirectory);
            }

            File bungeeJar = new File(temporaryDirectory, "BungeeCord.jar");
            if (!bungeeJar.exists()) {
                TimoCloudBase.getInstance().severe("Could not start proxy " + proxy.getName() + " because BungeeCord.jar does not exist. " + (
                        proxy.isStatic() ? "Please make sure the file " + bungeeJar.getAbsolutePath() + " exists (case sensitive!)."
                                : "Please make sure to have a file called 'BungeeCord.jar' in your template."));
                throw new ProxyStartException("BungeeCord.jar does not exist");
            }

            File plugins = new File(temporaryDirectory, "/plugins/");
            plugins.mkdirs();
            File plugin = new File(plugins, "TimoCloud.jar");
            if (plugin.exists()) plugin.delete();
            try {
                Files.copy(new File(TimoCloudBase.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath(), plugin.toPath());
            } catch (Exception e) {
                TimoCloudBase.getInstance().severe("Error while copying plugin into template:");
                TimoCloudBase.getInstance().severe(e);
                throw new ProxyStartException("Could not copy TimoCloud.jar into template");
            }

            Integer proxyPort = getFreePortCommon(PROXY_PORT_START, currentProxyPort, PROXY_PORT_MAX);
            currentProxyPort = proxyPort + 1;

            PublicKey publicKey = new RSAKeyPairRetriever(new File(temporaryDirectory, "plugins/TimoCloud/keys/")).generateKeyPair().getPublic();

            File configFile = new File(temporaryDirectory, "config.yml");
            configFile.createNewFile();
            Yaml yaml = new Yaml();
            Map<String, Object> config = (Map<String, Object>) yaml.load(new FileReader(configFile));
            if (config == null) config = new LinkedHashMap<>();
            config.put("player_limit", proxy.getMaxPlayersPerProxy());
            List<Map<String, Object>> listeners = (List) config.get("listeners");
            if (listeners == null) listeners = new ArrayList<>();
            Map<String, Object> map = listeners.size() == 0 ? new LinkedHashMap<>() : listeners.get(0);
            map.put("motd", proxy.getMotd());
            if (proxy.isStatic() && map.containsKey("host")) {
                proxyPort = Integer.parseInt(((String) map.get("host")).split(":")[1]);
            }
            map.put("force_default_server", false);
            map.put("host", "0.0.0.0:" + proxyPort);
            map.put("max_players", proxy.getMaxPlayers());
            if (!proxy.isStatic()) map.put("query_enabled", false);
            if (listeners.size() == 0) listeners.add(map);
            FileWriter writer = new FileWriter(configFile);
            config.put("listeners", listeners);
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            new Yaml(dumperOptions).dump(config, writer);

            double millisNow = System.currentTimeMillis();
            TimoCloudBase.getInstance().info("Successfully prepared starting proxy " + proxy.getName() + " in " + (millisNow - millisBefore) / 1000 + " seconds.");

            File logFile = getProxyLogFile(proxy.getId());
            logFile.createNewFile();

            FileTailer logTailer = generateLogTailer(logFile, (logEntry) -> {
                TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create()
                        .setType(MessageType.PROXY_LOG_ENTRY)
                        .setData(logEntry)
                        .setTarget(proxy.getId()));
            });
            new Thread(logTailer).start();
            this.logTailers.put(proxy.getId(), logTailer);

            try {
                String logString = "";
                if (getScreenVersion() >= 40602) {
                    logString = " -L -Logfile " + logFile.getAbsolutePath();
                }

                Process p = new ProcessBuilder(
                        "/bin/sh", "-c",
                        "screen -mdS " + proxy.getId() +
                                logString +
                                " /bin/sh -c '" +
                                "cd " + temporaryDirectory.getAbsolutePath() + " &&" +
                                " " + proxy.getJrePath() + " -server" +
                                " -Xmx" + proxy.getRam() + "M " +
                                buildStartParameters(proxy.getJavaParameters()) +
                                " -Dcom.mojang.eula.agree=true" +
                                " -Dtimocloud-proxyname=" + proxy.getName() +
                                " -Dtimocloud-proxyid=" + proxy.getId() +
                                " -Dtimocloud-corehost=" + TimoCloudBase.getInstance().getCoreSocketIP() + ":" + TimoCloudBase.getInstance().getCoreSocketPort() +
                                " -Dtimocloud-static=" + proxy.isStatic() +
                                " -Dtimocloud-templatedirectory=" + templateDirectory.getAbsolutePath() +
                                " -Dtimocloud-temporarydirectory=" + temporaryDirectory.getAbsolutePath() +
                                " -jar BungeeCord.jar" +
                                "'"
                ).start();

                TimoCloudBase.getInstance().info("Successfully started proxy screen session " + proxy.getName() + ".");
            } catch (Exception e) {
                TimoCloudBase.getInstance().severe("Error while starting proxy " + proxy.getName() + ":");
                throw new ProxyStartException("Error while starting process");
            }
            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create()
                    .setType(MessageType.BASE_PROXY_STARTED)
                    .setTarget(proxy.getId())
                    .set("port", proxyPort)
                    .set("publicKey", RSAKeyUtil.publicKeyToBase64(publicKey))
            );

        } catch (Exception e) {
            TimoCloudBase.getInstance().severe("Error while starting proxy " + proxy.getName() + ": " + e.getMessage());
            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.BASE_PROXY_NOT_STARTED).setTarget(proxy.getId()));
        }
    }

    private Integer getFreePortCommon(int startPort, int currentPort, int maxPort) throws InstanceStartException {
        if (currentPort == maxPort) currentPort = startPort;

        for (int i = 0; i <= maxPort - startPort; i++) {
            int port = (currentPort + i) % maxPort;
            if (port < startPort) port += startPort;
            if (isPortFree(port)) {
                return port;
            }
        }
        throw new InstanceStartException("No free port found. Please report this!");
    }

    private int getScreenVersion() {
        try {
            Process getVersion = new ProcessBuilder("/bin/sh", "-c", "screen -v").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(getVersion.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder textBuilder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
            }
            String log = textBuilder.toString().toLowerCase().trim().replace("screen version ", "").split(" ")[0].replaceAll("[^0-9]+", "");
            String version = log.replace(".", "");
            return Integer.parseInt(version);
        } catch (Exception exception) {
            TimoCloudBase.getInstance().warning("Error while getting Screen Version:");
            TimoCloudBase.getInstance().warning(exception.getMessage());
        }

        return Integer.MAX_VALUE;
    }

    private boolean isPortFree(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (Exception e) {
                TimoCloudBase.getInstance().severe(e);
            }
        }
    }

    private void setProperty(File file, String property, String value) {
        try {
            file.createNewFile();

            FileInputStream in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream(file);
            props.setProperty(property, value);
            props.store(out, null);
            out.close();
        } catch (Exception e) {
            TimoCloudBase.getInstance().severe("Error while setting property '" + property + "' to value '" + value + "' in file " + file.getAbsolutePath() + ":");
            TimoCloudBase.getInstance().severe(e);
        }
    }

    private FileTailer generateLogTailer(File logFile, Consumer<LogEntry> onMessage) {
        LogEntryReader logEntryReader = new LogEntryReader(onMessage);
        return new FileTailer(logFile, new LogTailerListener(logEntryReader), 500);
    }

    public void onServerStopped(String id) {
        FileTailer tailer = logTailers.get(id);
        if (tailer != null) {
            tailer.stop();
            logTailers.remove(id);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                File directory = new File(TimoCloudBase.getInstance().getFileManager().getServerTemporaryDirectory(), id);
                BaseFileManager.deleteDirectory(directory);
                getServerLogFile(id).delete();
            }
        }, 5 * 60 * 1000);
    }

    public void onProxyStopped(String id) {
        FileTailer tailer = logTailers.get(id);
        if (tailer != null) {
            tailer.stop();
            logTailers.remove(id);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                File directory = new File(TimoCloudBase.getInstance().getFileManager().getProxyTemporaryDirectory(), id);
                BaseFileManager.deleteDirectory(directory);
                getProxyLogFile(id).delete();
            }
        }, 5 * 60 * 1000);
    }

    private static File getServerLogFile(String id) {
        return new File(TimoCloudBase.getInstance().getFileManager().getServerLogsDirectory(), id + ".log");
    }

    private static File getProxyLogFile(String id) {
        return new File(TimoCloudBase.getInstance().getFileManager().getProxyLogsDirectory(), id + ".log");
    }

    public boolean isDownloadingTemplate() {
        return downloadingTemplate;
    }

    public void setDownloadingTemplate(boolean downloadingTemplate) {
        this.downloadingTemplate = downloadingTemplate;
    }

    private String buildStartParameters(List<String> parameters) {
        return parameters.stream().filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

}
