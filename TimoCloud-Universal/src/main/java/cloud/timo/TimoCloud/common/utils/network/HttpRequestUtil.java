package cloud.timo.TimoCloud.common.utils.network;

import cloud.timo.TimoCloud.common.objects.HttpRequestProperty;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestUtil {

    public static String request(String urlString, String requestMethod, String data, HttpRequestProperty... properties) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        for (HttpRequestProperty property : properties)
            connection.setRequestProperty(property.getKey(), property.getValue());
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.connect();
        if (data != null) {
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(data);
            dataOutputStream.flush();
        }
        InputStream inputStream = connection.getResponseCode() < 400 ? connection.getInputStream() : connection.getErrorStream();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        connection.disconnect();
        return stringBuilder.toString();
    }

    public static JsonElement requestJson(String urlString, String requestMethod, String data, HttpRequestProperty... properties) throws Exception {
        return new JsonParser().parse(request(urlString, requestMethod, data, properties));
    }

    public static Object requestJson(String urlString, String requestMethod, HttpRequestProperty... properties) throws Exception {
        return requestJson(urlString, requestMethod, null, properties);
    }

}
