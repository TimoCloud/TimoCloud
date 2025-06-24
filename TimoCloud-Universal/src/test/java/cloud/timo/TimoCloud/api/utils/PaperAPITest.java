package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.utils.paperapi.PaperAPI;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class PaperAPITest {

    // This test checks if the PaperAPI is working correctly
    @Test
    public void paperAPITest() throws MalformedURLException {
        for (PaperAPI.Project value : PaperAPI.Project.values()) {
            List<String> versions = PaperAPI.getVersions(value);
            for (String version : versions) {
                JsonObject latestBuilds = PaperAPI.getLatestBuilds(value, version);
                int latestBuild = latestBuilds.get("build").getAsInt();
                String fileName = latestBuilds.getAsJsonObject("downloads").getAsJsonObject("application").get("name").getAsString();
                String downloadURL = PaperAPI.buildDownloadURL(value, version, latestBuild, fileName);
                boolean downloadLink = isDownloadLink(downloadURL);
                assertTrue("Download of project " + value.getName() + " failed", downloadLink);
            }
        }
        assertTrue("No Paper-Projects support Proxy", Arrays.stream(PaperAPI.Project.values()).anyMatch(project -> project.isSupported(Proxy.class)));
        assertTrue("No Server-Projects support Proxy", Arrays.stream(PaperAPI.Project.values()).anyMatch(project -> project.isSupported(Server.class)));


    }

    private boolean isDownloadLink(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);

            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                return false;
            }

            String contentType = conn.getContentType();
            String contentDisposition = conn.getHeaderField("Content-Disposition");

            return (contentType != null && (
                    contentType.contains("application/octet-stream") ||
                            contentType.contains("application/java-archive") ||
                            contentType.contains("binary") ||
                            contentType.contains("zip") ||
                            contentType.contains("jar"))
            )
                    || (contentDisposition != null && contentDisposition.contains("attachment"));
        } catch (Exception e) {
            return false;
        }

    }
}
