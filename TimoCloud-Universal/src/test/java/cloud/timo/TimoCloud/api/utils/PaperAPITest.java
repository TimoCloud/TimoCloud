package cloud.timo.TimoCloud.api.utils;

import cloud.timo.TimoCloud.TimoCloudTest;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.utils.paperapi.PaperAPI;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class PaperAPITest {

    // This test checks if the PaperAPI is working correctly
    @Test
    public void paperAPITest() {
        for (PaperAPI.Project value : PaperAPI.Project.values()) {
            List<String> versions = PaperAPI.getVersions(value);
            for (String version : versions) {
                JsonObject latestBuilds = PaperAPI.getLatestBuilds(value, version);
                int latestBuild = latestBuilds.get("build").getAsInt();
                String fileName = latestBuilds.getAsJsonObject("downloads").getAsJsonObject("application").get("name").getAsString();
                String downloadURL = PaperAPI.buildDownloadURL(value, version, latestBuild, fileName);
                try {
                    String contentType = getContentType(URI.create(downloadURL).toURL());
                    assertTrue("Download of project " + value.getName() + " failed", contentType.startsWith("application/java-archive"));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        assertTrue("No Paper-Projects support Proxy", Arrays.stream(PaperAPI.Project.values()).anyMatch(project -> project.isSupported(Proxy.class)));
        assertTrue("No Server-Projects support Proxy", Arrays.stream(PaperAPI.Project.values()).anyMatch(project -> project.isSupported(Server.class)));


    }

    /**
     * Returns the value of the URL content-type header field.
     * It calls the URL's <code>URLConnection.getContentType</code> method
     * after retrieving a URLConnection object.
     * <i>Note: this method attempts to call the <code>openConnection</code>
     * method on the URL. If this method fails, or if a content type is not
     * returned from the URLConnection, getContentType returns
     * "application/octet-stream" as the content type.</i>
     *
     * @return the content type.
     */
    public String getContentType(URL url) {
        String type = null;
        try {
            type = url.openConnection().getContentType();
        } catch (IOException e) {
        }

        if (type == null)
            type = "application/octet-stream";

        return type;
    }

}
