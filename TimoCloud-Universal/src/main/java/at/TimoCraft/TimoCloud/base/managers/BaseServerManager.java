package at.TimoCraft.TimoCloud.base.managers;

import at.TimoCraft.TimoCloud.base.Base;
import at.TimoCraft.TimoCloud.base.objects.BaseServerObject;
import at.TimoCraft.TimoCloud.utils.FileAttributeUtil;
import at.TimoCraft.TimoCloud.utils.ServerToGroupUtil;
import at.TimoCraft.TimoCloud.utils.TimeUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BaseServerManager {

    private static final long STATIC_CREATE_TIME = 1482773874000L;

    private LinkedList<BaseServerObject> queue;
    private final ScheduledExecutorService scheduler;

    public BaseServerManager(long millis) {
        queue = new LinkedList<>();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::startNext, millis, millis, TimeUnit.MILLISECONDS);
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
        startServer(server);
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
        Base.info("Starting server " + server.getName() + "...");
        double millisBefore = System.currentTimeMillis();
        try {
            File templateDirectory = new File((server.isStatic() ? Base.getInstance().getFileManager().getStaticDirectory() : Base.getInstance().getFileManager().getTemplatesDirectory()), server.getGroup());
            if (!templateDirectory.exists()) {
                Base.severe("Could not start server " + server.getName() + ": No template called " + server.getGroup() + " found. Please make sure the directory " + templateDirectory.getAbsolutePath() + " exists. (Put your minecraft server in there)");
                return;
            }

            File temporaryDirectory = server.isStatic() ? templateDirectory : new File(Base.getInstance().getFileManager().getTemporaryDirectory(), server.getName());
            if (!server.isStatic()) {
                if (temporaryDirectory.exists()) deleteDirectory(temporaryDirectory);
                copyDirectory(Base.getInstance().getFileManager().getGlobalDirectory(), temporaryDirectory);
            }

            if (server.isStatic()) {
                copyDirectoryCarefully(Base.getInstance().getFileManager().getGlobalDirectory(), temporaryDirectory,  STATIC_CREATE_TIME, 1);
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
                Base.severe("Could not start server " + server.getName() + " because spigot.jar does not exist. Please make sure a the file " + spigotJar.getAbsolutePath() + " exists (case sensitive!)");
                return;
            }


            File plugins = new File(temporaryDirectory, "/plugins/");
            plugins.mkdirs();
            File plugin = new File(plugins, "TimoCloud.jar");
            if (plugin.exists()) {
                plugin.delete();
            }
            try {
                Files.copy(new File(Base.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath(), plugin.toPath());
            } catch (Exception e) {
                Base.severe("Error while copying plugin into template:");
                e.printStackTrace();
                if (!plugin.exists()) {
                    return;
                }
            }

            File serverProperties = new File(temporaryDirectory, "server.properties");
            setProperty(serverProperties, "online-mode", "false");
            setProperty(serverProperties, "server-name", server.getName());

            double millisNow = System.currentTimeMillis();
            Base.info("Successfully prepared starting server " + server.getName() + " in " + (millisNow - millisBefore) / 1000 + " seconds.");


            ProcessBuilder pb = new ProcessBuilder(
                    "/bin/sh", "-c",
                    "screen -mdS " + server.getName() +
                            " java -server " +
                            " -Xmx" + server.getRam() + "M" +
                            " -Dfile.encoding=UTF8 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:MaxGCPauseMillis=10 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 " +
                            " -Dcom.mojang.eula.agree=true" +
                            " -Dtimocloud-bungeecordhost=" + Base.getInstance().getBungeeSocketIP() + ":" + Base.getInstance().getBungeeSocketPort() +
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
                Base.info("Successfully started screen session " + server.getName() + ".");
            } catch (Exception e) {
                Base.severe("Error while starting server " + server.getName() + ":");
                e.printStackTrace();
            }
        } catch (Exception e) {
            Base.severe("Error while starting server " + server.getName() + ":");
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
            Base.severe("Error while setting property '" + property + "' to value '" + value + "' in file " + file.getAbsolutePath() + ":");
            e.printStackTrace();
        }
    }

    private File getRandomServer(String group) {
        File templates = Base.getInstance().getFileManager().getTemplatesDirectory();
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
        File directory = new File(Base.getInstance().getFileManager().getTemporaryDirectory(), name);
        if ((Boolean) Base.getInstance().getFileManager().getConfig().get("save-logs")) {
            File log = new File(directory, "/logs/latest.log");
            if (log.exists()) {
                try {
                    File dir = new File(Base.getInstance().getFileManager().getLogsDirectory(), ServerToGroupUtil.getGroupByServer(name));
                    dir.mkdirs();
                    Files.copy(log.toPath(), new File(dir, TimeUtil.formatTime() + "_" + name + ".log").toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Base.severe("No log from server " + name + " exists.");
            }
        }
        deleteDirectory(directory);
    }
}
