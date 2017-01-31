package at.TimoCraft.TimoCloud.base.managers;

import at.TimoCraft.TimoCloud.base.Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by Timo on 31.01.17.
 */
public class ServerManager {
    public void startServer(String name, int port, int ram) {

        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash", "-c",
                "cd " + new File(Base.getInstance().getFileManager().getTemporaryDirectory(), name).getAbsolutePath() + " &&" +
                        " screen -mdS " + name +
                        " java -server " +
                        " -Xmx" + ram + "M" +
                        " -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -XX:MaxGCPauseMillis=50 -XX:GCPauseIntervalMillis=100 -XX:+UseAdaptiveSizePolicy -XX:ParallelGCThreads=2 -XX:UseSSE=3 " +
                        " -Dcom.mojang.eula.agree=true" +
                        " -Dbungeecord-host=" + Base.getInstance().getBungeeSocketIP() + ":" + Base.getInstance().getBungeeSocketPort() +
                        " -jar spigot.jar -o false -h 0.0.0.0 -p " +
                        port)
                .directory(new File(Base.getInstance().getFileManager().getTemporaryDirectory(), name));
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
}
