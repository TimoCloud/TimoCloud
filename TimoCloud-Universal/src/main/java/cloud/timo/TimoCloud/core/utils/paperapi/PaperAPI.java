package cloud.timo.TimoCloud.core.utils.paperapi;

import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PaperAPI {

    public static final String PAPER_API_URL = "https://fill.papermc.io/v3/";

    public static List<String> getVersions(Project project) {
        String requestUrl = PAPER_API_URL + "projects/" + project.getName() + "/versions";
        List<String> versions = new ArrayList<>();
        try {
            JsonObject json = getJson(requestUrl);
            json.getAsJsonArray("versions").forEach(jsonElement -> {
                JsonObject version = jsonElement.getAsJsonObject();
                // Fill lists versions that have no build yet (e.g. velocity 3.5.0); builds/latest returns 404 for them
                JsonArray builds = version.getAsJsonArray("builds");
                if (builds != null && !builds.isEmpty()) {
                    versions.add(version.getAsJsonObject("version").get("id").getAsString());
                }
            });
        } catch (IOException ignored) {
        }
        return versions;
    }

    public static JsonObject getJson(String url) throws IOException {
        String json = IOUtils.toString(URI.create(url), StandardCharsets.UTF_8);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    @SneakyThrows
    public static void download(String url, File dest) {
        FileUtils.copyURLToFile(URI.create(url).toURL(), dest);
    }

    public static String buildDownloadURL(JsonObject build) {
        return build.getAsJsonObject("downloads").getAsJsonObject("server:default").get("url").getAsString();
    }
    public static String getFileName(JsonObject build) {
                return build.getAsJsonObject("downloads").getAsJsonObject("server:default").get("name").getAsString();
    }

    public static JsonObject getLatestBuilds(Project project, String version) {
        String requestUrl = PAPER_API_URL + "projects/" + project.getName() + "/versions/" + version + "/builds/latest";
        try {
            JsonObject latest = getJson(requestUrl).getAsJsonObject();
            return latest;
        } catch (IOException e) {
            return null;
        }
    }

    public enum Project {
        PAPER("paper", Server.class),
        WATERFALL("waterfall", Proxy.class),
        VELOCITY("velocity", Proxy.class),
        FOLIA("folia");

        private final String name;
        private final Class<?> clazz;

        Project(String name) {
            this.name = name;
            this.clazz = null;
        }

        Project(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public String getName() {
            return name;
        }

        public boolean isSupported(Class<?> clazz) {
            return this.clazz != null && this.clazz.isAssignableFrom(clazz);
        }

        public static Project getByName(String name) {
            for (Project project : values()) {
                if (project.getName().equalsIgnoreCase(name)) {
                    return project;
                }
            }
            return null;
        }
    }
}
