package me.yanvsky.unityegg.other;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LicenseKey {

    private static final String LICENSE_API_BASE_URL = "https://api.licensegate.io/license/";

    public static boolean isLicenseValid(String licenseKey) {
        if (licenseKey == null || licenseKey.isEmpty()) {
            return false;
        }

        try {
            URL url = new URL(LICENSE_API_BASE_URL + licenseKey + "/verify");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.optBoolean("valid", false);

        } catch (Exception e) {
            return false;
        }
    }
}
