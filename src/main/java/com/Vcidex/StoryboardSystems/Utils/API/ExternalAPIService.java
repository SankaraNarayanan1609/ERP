package com.Vcidex.StoryboardSystems.Utils.API;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
public class ExternalAPIService {
    private static final String API_ENDPOINT = "https://api.example.com/productType";

    public static String fetchProductTypeFromAPI(String poId) {
        try {
            URI uri = new URI(API_ENDPOINT + "?poId=" + poId);
            URL url = uri.toURL(); // ✅ Convert URI to URL

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                return "{\"productType\": \"UNKNOWN\"}";
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.optString("productType", "UNKNOWN");
        } catch (Exception e) {
            return "{\"productType\": \"UNKNOWN\"}";
        }
    }
}