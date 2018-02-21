package cloud.timo.TimoCloud.base.managers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.exceptions.ProxyStartException;
import cloud.timo.TimoCloud.base.exceptions.ServerStartException;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.lib.utils.HashUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BaseServerManager {

    private static final long STATIC_CREATE_TIME = 1482773874000L;

    private LinkedList<BaseServerObject> serverQueue;
    private LinkedList<BaseProxyObject> proxyQueue;

    private Map<Integer, Integer> recentlyUsedPorts;

    private final ScheduledExecutorService scheduler;

    private boolean startingServer = false;
    private boolean startingProxy = false;

    public BaseServerManager(long millis) {
        serverQueue = new LinkedList<>();
        proxyQueue = new LinkedList<>();
        recentlyUsedPorts = new HashMap<>();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::everySecond, millis, millis, TimeUnit.MILLISECONDS);
    }

    public void updateResources() {
        double cpu = TimoCloudBase.getInstance().getResourceManager().getCpuUsage();
        boolean ready = serverQueue.isEmpty() && proxyQueue.isEmpty() && !startingServer && !startingProxy && cpu <= (Double) TimoCloudBase.getInstance().getFileManager().getConfig().get("cpu-max-load");
        long freeRam = Math.max(0, TimoCloudBase.getInstance().getResourceManager().getFreeMemory() - ((Integer) TimoCloudBase.getInstance().getFileManager().getConfig().get("ram-keep-free")).longValue());
        Map<String, Object> resourcesMap = new HashMap<>();
        resourcesMap.put("ready", ready);
        resourcesMap.put("availableRam", freeRam);
        resourcesMap.put("maxRam", TimoCloudBase.getInstance().getFileManager().getConfig().get("ram"));
        resourcesMap.put("cpu", cpu);
        TimoCloudBase.getInstance().getSocketMessageManager().sendMessage("RESOURCES", new JSONObject(resourcesMap));
    }

    public void addToServerQueue(BaseServerObject server) {
        serverQueue.add(server);
    }

    public void addToProxyQueue(BaseProxyObject proxy) {
        proxyQueue.add(proxy);
    }

    private void everySecond() {
        try {
            countDownPorts();
            startNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startNext() {
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

    private void deleteDirectory(File directory) {
        if (directory.exists()) FileDeleteStrategy.FORCE.deleteQuietly(directory);
    }

    private void startServer(BaseServerObject server) {
        TimoCloudBase.info("Starting server " + server.getName() + "...");
        double millisBefore = System.currentTimeMillis();
        try {
            File templateDirectory = new File((server.isStatic() ? TimoCloudBase.getInstance().getFileManager().getServerStaticDirectory() : TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory()), server.getGroup());
            if (!templateDirectory.exists()) templateDirectory.mkdirs();

            File mapDirectory = new File(TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory(), server.getGroup() + "_" + server.getMap());

            if (! server.isStatic()) {
                JSONObject templateHashes = server.isStatic() ? null : HashUtil.getHashes(templateDirectory);
                JSONObject mapHashes = (!server.isStatic() && server.getMapHash() != null) ? HashUtil.getHashes(mapDirectory) : null;
                JSONObject globalHashes = HashUtil.getHashes(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory());

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
                    Map<String, Object> differences = new HashMap<>();
                    if (templateDifferences.size() > 0) differences.put("templateDifferences", templateDifferences);
                    if (mapDifferences.size() > 0) differences.put("mapDifferences", mapDifferences);
                    if (globalDifferences.size() > 0) differences.put("globalDifferences", globalDifferences);
                    TimoCloudBase.info("New server template updates found! Stopping and downloading updates...");
                    JSONObject differencesJson = new JSONObject(differences);
                    Map<String, Object> message = new HashMap<>();
                    message.put("type", "SERVER_TEMPLATE_REQUEST");
                    message.put("target", server.getToken());
                    if (templateDifferences.size() > 0) message.put("template", templateDirectory.getName());
                    if (mapDifferences.size() > 0) message.put("map", server.getMap());
                    message.put("differences", differencesJson);
                    TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(new JSONObject(message));
                    return;
                }
            }

            File temporaryDirectory = server.isStatic() ? templateDirectory : new File(TimoCloudBase.getInstance().getFileManager().getServerTemporaryDirectory(), server.getName() + "_" + server.getToken());
            if (!server.isStatic()) {
                if (temporaryDirectory.exists()) deleteDirectory(temporaryDirectory);
                copyDirectory(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory(), temporaryDirectory);
            }

            if (server.isStatic()) {
                copyDirectoryCarefully(TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory(), temporaryDirectory, STATIC_CREATE_TIME, 1);
            } else {
                copyDirectory(templateDirectory, temporaryDirectory);
            }

            boolean randomMap = false;
            String mapName = server.getMap() == null ? "default" : server.getMap();
            if (!server.isStatic() && server.getMap() != null) {
                randomMap = true;
                copyDirectory(mapDirectory, temporaryDirectory);
            }

            File spigotJar = new File(temporaryDirectory, "spigot.jar");
            if (!spigotJar.exists()) {
                TimoCloudBase.severe("Could not start server " + server.getName() + " because spigot.jar does not exist. Please make sure a the file " + spigotJar.getAbsolutePath() + " exists (case sensitive!)");
                throw new ServerStartException("spigot.jar does not exist");
            }


            File plugins = new File(temporaryDirectory, "/plugins/");
            plugins.mkdirs();
            File plugin = new File(plugins, "TimoCloud.jar");
            if (plugin.exists()) plugin.delete();
            try {
                Files.copy(new File(TimoCloudBase.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath(), plugin.toPath());
            } catch (Exception e) {
                TimoCloudBase.severe("Error while copying plugin into template:");
                e.printStackTrace();
                throw new ServerStartException("Could not copy TimoCloud.jar into template");
            }

            Integer port = getFreePort();
            if (port == null) {
                TimoCloudBase.severe("Error while starting server " + server.getName() + ": No free port found. Please report this!");
                throw new ServerStartException("No free port found");
            }
            blockPort(port);

            File serverProperties = new File(temporaryDirectory, "server.properties");
            setProperty(serverProperties, "online-mode", "false");
            setProperty(serverProperties, "server-name", server.getName());

            double millisNow = System.currentTimeMillis();
            TimoCloudBase.info("Successfully prepared starting server " + server.getName() + " in " + (millisNow - millisBefore) / 1000 + " seconds.");


            ProcessBuilder pb = new ProcessBuilder(
                    "/bin/sh", "-c",
                    "screen -mdS " + server.getName() +
                            " java -server " +
                            " -Xmx" + server.getRam() + "M" +
                            " -Dfile.encoding=UTF8 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:MaxGCPauseMillis=10 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 " +
                            " -Dcom.mojang.eula.agree=true" +
                            " -Dtimocloud-corehost=" + TimoCloudBase.getInstance().getCoreSocketIP() + ":" + TimoCloudBase.getInstance().getCoreSocketPort() +
                            " -Dtimocloud-randommap=" + randomMap +
                            " -Dtimocloud-mapname=" + mapName +
                            " -Dtimocloud-servername=" + server.getName() +
                            " -Dtimocloud-static=" + server.isStatic() +
                            " -Dtimocloud-token=" + server.getToken() +
                            " -Dtimocloud-templatedirectory=" + templateDirectory.getAbsolutePath() +
                            " -Dtimocloud-temporarydirectory=" + temporaryDirectory.getAbsolutePath() +
                            " -jar spigot.jar -o false -h 0.0.0.0 -p " + port
            ).directory(temporaryDirectory);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                TimoCloudBase.info("Successfully started screen session " + server.getName() + ".");
            } catch (Exception e) {
                TimoCloudBase.severe("Error while starting server " + server.getName() + ":");
                e.printStackTrace();
                throw new ServerStartException("Could not start process");
            }

            Map<String, Object> message = new HashMap<>();
            message.put("type", "SERVER_STARTED");
            message.put("target", server.getToken());
            message.put("port", port);
            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(new JSONObject(message));

        } catch (Exception e) {
            TimoCloudBase.severe("Error while starting server " + server.getName() + ":");
            e.printStackTrace();
            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage("SERVER_NOT_STARTED", server.getToken(), server.getToken());
        }
    }


    private void startProxy(BaseProxyObject proxy) {
        TimoCloudBase.info("Starting proxy " + proxy.getName() + "...");
        double millisBefore = System.currentTimeMillis();
        try {
            File templateDirectory = new File((proxy.isStatic() ? TimoCloudBase.getInstance().getFileManager().getProxyStaticDirectory() : TimoCloudBase.getInstance().getFileManager().getProxyTemplatesDirectory()), proxy.getGroup());
            if (!templateDirectory.exists()) templateDirectory.mkdirs();

            if (! proxy.isStatic()) {
                JSONObject templateHashes = HashUtil.getHashes(templateDirectory);
                JSONObject globalHashes = HashUtil.getHashes(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory());

                HashUtil.deleteIfNotExisting(templateDirectory, "", templateHashes, proxy.getTemplateHash());
                HashUtil.deleteIfNotExisting(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory(), "", globalHashes, proxy.getGlobalHash());

                templateHashes = HashUtil.getHashes(templateDirectory);
                globalHashes = HashUtil.getHashes(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory());

                List<String> templateDifferences = proxy.isStatic() ? new ArrayList<>() : HashUtil.getDifferentFiles("", proxy.getTemplateHash(), templateHashes);
                List<String> gloalDifferences = HashUtil.getDifferentFiles("", proxy.getGlobalHash(), globalHashes);

                if (templateDifferences.size() > 0 || gloalDifferences.size() > 0) {
                    Map<String, Object> differences = new HashMap<>();
                    if (templateDifferences.size() > 0) differences.put("templateDifferences", templateDifferences);
                    if (gloalDifferences.size() > 0) differences.put("globalDifferences", gloalDifferences);
                    TimoCloudBase.info("New proxy template updates found! Stopping and downloading updates...");
                    JSONObject differencesJson = new JSONObject(differences);
                    Map<String, Object> message = new HashMap<>();
                    message.put("type", "PROXY_TEMPLATE_REQUEST");
                    message.put("target", proxy.getToken());
                    if (templateDifferences.size() > 0) message.put("template", templateDirectory.getName());
                    message.put("differences", differencesJson);
                    TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(new JSONObject(message));
                    return;
                }
            }

            File temporaryDirectory = proxy.isStatic() ? templateDirectory : new File(TimoCloudBase.getInstance().getFileManager().getProxyTemporaryDirectory(), proxy.getName() + "_" + proxy.getToken());
            if (!proxy.isStatic()) {
                if (temporaryDirectory.exists()) deleteDirectory(temporaryDirectory);
                copyDirectory(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory(), temporaryDirectory);
            }

            if (proxy.isStatic()) {
                copyDirectoryCarefully(TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory(), temporaryDirectory, STATIC_CREATE_TIME, 1);
            } else {
                copyDirectory(templateDirectory, temporaryDirectory);
            }

            File bungeeJar = new File(temporaryDirectory, "BungeeCord.jar");
            if (!bungeeJar.exists()) {
                TimoCloudBase.severe("Could not start proxy " + proxy.getName() + " because BungeeCord.jar does not exist. Please make sure a the file " + bungeeJar.getAbsolutePath() + " exists (case sensitive!)");
                throw new ProxyStartException("BungeeCord.jar does not exist");
            }


            File plugins = new File(temporaryDirectory, "/plugins/");
            plugins.mkdirs();
            File plugin = new File(plugins, "TimoCloud.jar");
            if (plugin.exists()) plugin.delete();
            try {
                Files.copy(new File(TimoCloudBase.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath(), plugin.toPath());
            } catch (Exception e) {
                TimoCloudBase.severe("Error while copying plugin into template:");
                e.printStackTrace();
                throw new ProxyStartException("Could not copy TimoCloud.jar into template");
            }

            Integer port = getFreePort();
            if (port == null) {
                TimoCloudBase.severe("Error while starting proxy " + proxy.getName() + ": No free port found. Please report this!");
                throw new ProxyStartException("No free port found");
            }
            blockPort(port);

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
                port = Integer.parseInt(((String) map.get("host")).split(":")[1]);
            }
            map.put("host", "0.0.0.0:" + port);
            map.put("max_players", proxy.getMaxPlayers());
            map.put("query_enabled", false);
            if (listeners.size() == 0) listeners.add(map);
            FileWriter writer = new FileWriter(configFile);
            config.put("listeners", listeners);
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            new Yaml(dumperOptions).dump(config, writer);

            double millisNow = System.currentTimeMillis();
            TimoCloudBase.info("Successfully prepared starting proxy " + proxy.getName() + " in " + (millisNow - millisBefore) / 1000 + " seconds.");


            ProcessBuilder pb = new ProcessBuilder(
                    "/bin/sh", "-c",
                    "screen -mdS " + proxy.getName() +
                            " java -server " +
                            " -Xmx" + proxy.getRam() + "M" +
                            " -Dfile.encoding=UTF8 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:MaxGCPauseMillis=10 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 " +
                            //" -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" +
                            " -Dcom.mojang.eula.agree=true" +
                            " -Dtimocloud-corehost=" + TimoCloudBase.getInstance().getCoreSocketIP() + ":" + TimoCloudBase.getInstance().getCoreSocketPort() +
                            " -Dtimocloud-proxyname=" + proxy.getName() +
                            " -Dtimocloud-static=" + proxy.isStatic() +
                            " -Dtimocloud-token=" + proxy.getToken() +
                            " -Dtimocloud-templatedirectory=" + templateDirectory.getAbsolutePath() +
                            " -Dtimocloud-temporarydirectory=" + temporaryDirectory.getAbsolutePath() +
                            " -jar BungeeCord.jar"
            ).directory(temporaryDirectory);
            try {
                pb.start();
                TimoCloudBase.info("Successfully started screen session " + proxy.getName() + ".");
            } catch (Exception e) {
                TimoCloudBase.severe("Error while starting proxy " + proxy.getName() + ":");
                e.printStackTrace();
                throw new ProxyStartException("Error while starting process");
            }

            Map<String, Object> message = new HashMap<>();
            message.put("type", "PROXY_STARTED");
            message.put("target", proxy.getToken());
            message.put("port", port);
            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(new JSONObject(message));

        } catch (Exception e) {
            TimoCloudBase.severe("Error while starting proxy " + proxy.getName() + ":");
            e.printStackTrace();
            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage("PROXY_NOT_STARTED", proxy.getToken(), proxy.getToken());
        }
    }

    private Integer getFreePort() {
        for (int p = 40000; p<=50000; p++) {
            if (portIsFree(p)) return p;
        }
        return null;
    }

    private void blockPort(int port) {
        recentlyUsedPorts.put(port, 60);
    }

    private boolean portIsFree(int port) {
        if (recentlyUsedPorts.containsKey(port)) return false;
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
                e.printStackTrace();
            }
        }
    }

    private void countDownPorts() {
        List<Integer> remove = new ArrayList<>();
        for (Integer port : recentlyUsedPorts.keySet()) {
            recentlyUsedPorts.put(port, recentlyUsedPorts.get(port) - 1);
            if (recentlyUsedPorts.get(port) <= 0) remove.add(port);
        }
        for (Integer port : remove) recentlyUsedPorts.remove(port);
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
            TimoCloudBase.severe("Error while setting property '" + property + "' to value '" + value + "' in file " + file.getAbsolutePath() + ":");
            e.printStackTrace();
        }
    }


    public void onServerStopped(String name, String token) {
        File directory = new File(TimoCloudBase.getInstance().getFileManager().getServerTemporaryDirectory(), name + "_" + token);
        /*
        if ((Boolean) TimoCloudBase.getInstance().getFileManager().getConfig().get("save-logs")) {
            File log = new File(directory, "/logs/latest.log");
            if (log.exists()) {
                try {
                    File dir = new File(TimoCloudBase.getInstance().getFileManager().getLogsDirectory(), ServerToGroupUtil.getGroupByServer(name));
                    dir.mkdirs();
                    Files.copy(log.toPath(), new File(dir, TimeUtil.formatTime() + "_" + name + ".log").toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                TimoCloudBase.severe("No log from server " + name + " exists.");
            }
        }
        */
        deleteDirectory(directory);
    }

    public void onProxyStopped(String name, String token) {
        File directory = new File(TimoCloudBase.getInstance().getFileManager().getProxyTemporaryDirectory(), name + "_" + token);
        /*
        if ((Boolean) TimoCloudBase.getInstance().getFileManager().getConfig().get("save-logs")) {
            File log = new File(directory, "proxy.log.0");
            if (log.exists()) {
                try {
                    File dir = new File(TimoCloudBase.getInstance().getFileManager().getLogsDirectory(), ServerToGroupUtil.getGroupByServer(name));
                    dir.mkdirs();
                    Files.copy(log.toPath(), new File(dir, TimeUtil.formatTime() + "_" + name + ".log").toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                TimoCloudBase.severe("No log from proxy " + name + " exists.");
            }
        }
        */
        deleteDirectory(directory);
    }
}
