package cloud.timo.TimoCloud.base.managers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.utils.ServerToGroupUtil;
import cloud.timo.TimoCloud.utils.TimeUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BaseServerManager {

    private static final long STATIC_CREATE_TIME = 1482773874000L;

    private LinkedList<BaseServerObject> queue;
    private final ScheduledExecutorService scheduler;
    private boolean startingServer;

    public BaseServerManager(long millis) {
        queue = new LinkedList<>();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::startNext, millis, millis, TimeUnit.MILLISECONDS);
        startingServer = false;
    }

    public void updateResources() {
        boolean ready = queue.isEmpty() && ! startingServer && TimoCloudBase.getInstance().getResourceManager().getCpuUsage() <= (Double) TimoCloudBase.getInstance().getFileManager().getConfig().get("cpu-max-load");
        int freeRam = Math.max(0, TimoCloudBase.getInstance().getResourceManager().getFreeMemory() - (Integer) TimoCloudBase.getInstance().getFileManager().getConfig().get("ram-keep-free"));
        Map<String, Object> resourcesMap = new HashMap<>();
        resourcesMap.put("ready", ready);
        resourcesMap.put("availableRam", freeRam);
        resourcesMap.put("maxRam", TimoCloudBase.getInstance().getFileManager().getConfig().get("ram"));
        TimoCloudBase.getInstance().getSocketMessageManager().sendMessage("RESOURCES", new JSONObject(resourcesMap));
    }

    public void addToQueue(BaseServerObject server) {
        queue.add(server);
    }

    public void startNext() {
        if (queue.isEmpty()) return;
        BaseServerObject server = queue.pop();
        if (server == null) {
            startNext();
            return;
        }
        startingServer = true;
        updateResources();
        startServer(server);
        startingServer = false;
        updateResources();
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
                copyDirectoryCarefully(file, toFile, value, layer+1);
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
            File templateDirectory = new File((server.isStatic() ? TimoCloudBase.getInstance().getFileManager().getStaticDirectory() : TimoCloudBase.getInstance().getFileManager().getTemplatesDirectory()), server.getGroup());
            if (!templateDirectory.exists()) {
                TimoCloudBase.severe("Could not start server " + server.getName() + ": No template called " + server.getGroup() + " found. Please make sure the directory " + templateDirectory.getAbsolutePath() + " exists. (Put your minecraft server in there)");
                return;
            }

            File temporaryDirectory = server.isStatic() ? templateDirectory : new File(TimoCloudBase.getInstance().getFileManager().getTemporaryDirectory(), server.getName());
            if (!server.isStatic()) {
                if (temporaryDirectory.exists()) deleteDirectory(temporaryDirectory);
                copyDirectory(TimoCloudBase.getInstance().getFileManager().getGlobalDirectory(), temporaryDirectory);
            }

            if (server.isStatic()) {
                copyDirectoryCarefully(TimoCloudBase.getInstance().getFileManager().getGlobalDirectory(), temporaryDirectory,  STATIC_CREATE_TIME, 1);
            } else {
                copyDirectory(templateDirectory, temporaryDirectory);
            }

            boolean randomMap = false;
            String mapName = "default";
            if (!server.isStatic()) {
                File randomMapDirectory = getRandomServer(server.getGroup());
                if (randomMapDirectory != null && randomMapDirectory.exists()) {
                    randomMap = true;
                    mapName = "";
                    String[] splitted = randomMapDirectory.getName().split("_");
                    for (int i = 1; i < splitted.length; i++) {
                        mapName += splitted[i];
                        if (i < splitted.length - 1) mapName += "_";
                    }
                    copyDirectory(randomMapDirectory, temporaryDirectory);
                }
            }

            File spigotJar = new File(temporaryDirectory, "spigot.jar");
            if (!spigotJar.exists()) {
                TimoCloudBase.severe("Could not start server " + server.getName() + " because spigot.jar does not exist. Please make sure a the file " + spigotJar.getAbsolutePath() + " exists (case sensitive!)");
                return;
            }


            File plugins = new File(temporaryDirectory, "/plugins/");
            plugins.mkdirs();
            File plugin = new File(plugins, "TimoCloudBungee.jar");
            if (plugin.exists()) {
                plugin.delete();
            }
            try {
                Files.copy(new File(TimoCloudBase.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath(), plugin.toPath());
            } catch (Exception e) {
                TimoCloudBase.severe("Error while copying plugin into template:");
                e.printStackTrace();
                if (!plugin.exists()) {
                    return;
                }
            }

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
                            " -Dtimocloud-bungeecordhost=" + TimoCloudBase.getInstance().getBungeeSocketIP() + ":" + TimoCloudBase.getInstance().getBungeeSocketPort() +
                            " -Dtimocloud-randommap=" + randomMap +
                            " -Dtimocloud-mapname=" + mapName +
                            " -Dtimocloud-servername=" + server.getName() +
                            " -Dtimocloud-static=" + server.isStatic() +
                            " -Dtimocloud-token=" + server.getToken() +
                            " -Dtimocloud-templatedirectory=" + templateDirectory.getAbsolutePath() +
                            " -Dtimocloud-temporarydirectory=" + temporaryDirectory.getAbsolutePath() +
                            " -jar spigot.jar -o false -h 0.0.0.0 -p " + server.getPort()
            ).directory(temporaryDirectory);

            try {
                pb.start();
                TimoCloudBase.info("Successfully started screen session " + server.getName() + ".");
            } catch (Exception e) {
                TimoCloudBase.severe("Error while starting server " + server.getName() + ":");
                e.printStackTrace();
            }
        } catch (Exception e) {
            TimoCloudBase.severe("Error while starting server " + server.getName() + ":");
            e.printStackTrace();
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
            TimoCloudBase.severe("Error while setting property '" + property + "' to value '" + value + "' in file " + file.getAbsolutePath() + ":");
            e.printStackTrace();
        }
    }

    private File getRandomServer(String group) {
        File templates = TimoCloudBase.getInstance().getFileManager().getTemplatesDirectory();
        List<File> valid = new ArrayList<>();
        for (File sub : templates.listFiles()) {
            if (!sub.isDirectory() || !sub.getName().startsWith(group + "_")) {
                continue;
            }
            valid.add(sub);
        }
        if (valid.size() < 1) {
            return null;
        }
        return valid.get(new Random().nextInt(valid.size()));
    }

    public void onServerStopped(String name) {
        File directory = new File(TimoCloudBase.getInstance().getFileManager().getTemporaryDirectory(), name);
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
        deleteDirectory(directory);
    }
}
