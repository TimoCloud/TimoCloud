package at.TimoCraft.TimoCloud.base.managers;

import at.TimoCraft.TimoCloud.base.Base;
import at.TimoCraft.TimoCloud.utils.TimeUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Timo on 31.01.17.
 */
public class ServerManager {
    public void startServer(String name, int port, int ram, boolean isStatic, String groupName) {
        Base.info("Starting server " + name + "...");
        double millisBefore = System.currentTimeMillis();

        File templatesDir = new File(Base.getInstance().getFileManager().getTemplatesDirectory() + groupName);
        boolean randomMap = false;
        String mapName = "default";
        if (! templatesDir.exists()) {
            templatesDir = getRandomServer(groupName);
            randomMap = true;
            mapName = "";
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
            Base.severe("Could not start server " + name + " because spigot.jar does not exist.");
            return;
        }
        try {
            File directory = new File(Base.getInstance().getFileManager().getTemporaryDirectory() + name);
            if (directory.exists() && !isStatic) {
                FileDeleteStrategy.FORCE.deleteQuietly(directory);
            }
            if (!isStatic) {
                FileUtils.copyDirectory(templatesDir, directory);
            }
            File plugin = new File(Base.getInstance().getFileManager().getTemporaryDirectory() + name + "/plugins/", Base.getInstance().getFileName());
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
            double millisNow = System.currentTimeMillis();
            Base.getInstance().info("Successfully started server " + name + " in " + (millisNow - millisBefore) / 1000 + " seconds.");
        } catch (Exception e) {
            Base.severe("Error while starting server " + name + ":");
            e.printStackTrace();
        }

        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash", "-c",
                "cd " + new File(Base.getInstance().getFileManager().getTemporaryDirectory(), name).getAbsolutePath() + " &&" +
                        " screen -mdS " + name +
                        " java -server " +
                        " -Xmx" + ram + "M" +
                        " -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:MaxGCPauseMillis=50 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 " +
                        " -Dcom.mojang.eula.agree=true" +
                        " -Dbungeecord-host=" + Base.getInstance().getBungeeSocketIP() + ":" + Base.getInstance().getBungeeSocketPort() +
                        " -Drandom-map=" + randomMap +
                        " -Dmap-name=" + mapName +
                        " -Dserver-name=" + name +
                        " -Dstatic=" + isStatic +
                        " -jar spigot.jar -o false -h 0.0.0.0 -p " + port
        ).directory(new File(Base.getInstance().getFileManager().getTemporaryDirectory(), name));
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Base.severe("Got response when starting server: " + line);
            }
        } catch (Exception e) {
            Base.severe("Error while starting server " + name + ":");
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
                File dir = new File(Base.getInstance().getFileManager().getLogsDirectory() + getGroupByServer(name));
                dir.mkdirs();
                Files.copy(log.toPath(), new File(dir, TimeUtil.formatTime() + "_" + name).toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Base.severe("No log from server " + name + " exists.");
        }
    }

    public String getGroupByServer(String server) {
        if (!server.contains("-")) {
            return server;
        }
        String ret = "";
        String[] split = server.split("-");
        for (int i = 0; i < split.length - 1; i++) {
            ret = ret + split[i];
            if (i < split.length - 2) {
                ret = ret + "-";
            }
        }
        return ret;
    }
}
