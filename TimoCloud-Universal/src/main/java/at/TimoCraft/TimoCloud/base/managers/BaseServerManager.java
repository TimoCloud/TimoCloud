package at.TimoCraft.TimoCloud.base.managers;

import at.TimoCraft.TimoCloud.base.Base;
import at.TimoCraft.TimoCloud.base.objects.BaseServerObject;
import at.TimoCraft.TimoCloud.utils.ServerToGroupUtil;
import at.TimoCraft.TimoCloud.utils.TimeUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Timo on 31.01.17.
 */
public class BaseServerManager {

    private LinkedList<BaseServerObject> queue;
    private final ScheduledExecutorService scheduler;

    public BaseServerManager(long millis) {
        queue = new LinkedList<>();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> startNext(), millis, millis, TimeUnit.MILLISECONDS);
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

    private void startServer(BaseServerObject server) {
        Base.info("Starting server " + server.getName() + "...");
        double millisBefore = System.currentTimeMillis();

        File templatesDir = new File(Base.getInstance().getFileManager().getTemplatesDirectory() + server.getGroup());
        boolean randomMap = false;
        String mapName = "default";
        if (! templatesDir.exists()) {
            templatesDir = getRandomServer(server.getGroup());
            randomMap = true;
            mapName = "";
            if (templatesDir.getName() == null) {
                Base.severe("Could not start server " + server.getName() + ": No template called " + server.getName() + " found.");
                return;
            }
            String[] splitted = templatesDir.getName().split("_");
            for (int i = 1; i<splitted.length; i++) {
                mapName += splitted[i];
                if (i < splitted.length-1) {
                    mapName += "_";
                }
            }
        }
        File spigot = new File(templatesDir, "spigot.jar");
        if (!spigot.exists()) {
            Base.severe("Could not start server " + server.getName() + " because spigot.jar does not exist.");
            return;
        }
        try {
            File directory = new File(Base.getInstance().getFileManager().getTemporaryDirectory() + server.getName());
            if (directory.exists() && !server.isStatic()) {
                FileDeleteStrategy.FORCE.deleteQuietly(directory);
            }
            if ((!server.isStatic()) || ! directory.exists()) {
                FileUtils.copyDirectory(templatesDir, directory);
            }
            File plugin = new File(new File(directory, "/plugins/"), Base.getInstance().getFileName());
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
            File pluginsDir = new File("plugins/");
            if (pluginsDir.exists() && pluginsDir.isDirectory()) {
                for (File pl : pluginsDir.listFiles()) {
                    File target = new File(directory, "plugins/" + pl.getName());
                    if (target.exists()) continue;
                    Files.copy(pl.toPath(), target.toPath());
                }
            }
            double millisNow = System.currentTimeMillis();
            Base.getInstance().info("Successfully started server " + server.getName() + " in " + (millisNow - millisBefore) / 1000 + " seconds.");
        } catch (Exception e) {
            Base.severe("Error while starting server " + server.getName() + ":");
            e.printStackTrace();
        }

        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash", "-c",
                "cd " + new File(Base.getInstance().getFileManager().getTemporaryDirectory(), server.getName()).getAbsolutePath() + " &&" +
                        " screen -mdS " + server.getName() +
                        " java -server " +
                        " -Xmx" + server.getRam() + "M" +
                        " -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:MaxGCPauseMillis=50 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 " +
                        " -Dcom.mojang.eula.agree=true" +
                        " -Dtimocloud-bungeecordhost=" + Base.getInstance().getBungeeSocketIP() + ":" + Base.getInstance().getBungeeSocketPort() +
                        " -Dtimocloud-randommap=" + randomMap +
                        " -Dtimocloud-mapname=" + mapName +
                        " -Dtimocloud-servername=" + server.getName() +
                        " -Dtimocloud-static=" + server.isStatic() +
                        " -Dtimocloud-token=" + server.getToken() +
                        " -Dtimocloud-template=" + templatesDir.getAbsolutePath() +
                        " -jar spigot.jar -o false -h 0.0.0.0 -p " + server.getPort()
        ).directory(new File(Base.getInstance().getFileManager().getTemporaryDirectory(), server.getName()));
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Base.severe("Got response when starting server: " + line);
            }
        } catch (Exception e) {
            Base.severe("Error while starting server " + server.getName() + ":");
            e.printStackTrace();
        }
    }

    private File getRandomServer(String group) {
        File templates = new File(Base.getInstance().getFileManager().getTemplatesDirectory());
        List<File> valid = new ArrayList<>();
        for (File sub : templates.listFiles()) {
            if (! sub.isDirectory() || ! sub.getName().startsWith(group + "_")) {
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
        File log = new File(Base.getInstance().getFileManager().getTemporaryDirectory() + name + "/logs/latest.log");
        if (log.exists()) {
            try {
                File dir = new File(Base.getInstance().getFileManager().getLogsDirectory() + ServerToGroupUtil.getGroupByServer(name));
                dir.mkdirs();
                Files.copy(log.toPath(), new File(dir, TimeUtil.formatTime() + "_" + name).toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Base.severe("No log from server " + name + " exists.");
        }
    }
}
