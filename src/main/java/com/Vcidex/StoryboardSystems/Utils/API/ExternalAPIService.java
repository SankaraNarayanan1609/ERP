package com.Vcidex.StoryboardSystems.Utils.API;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExternalAPIService {
    private static final Logger logger = LogManager.getLogger(ExternalAPIService.class);
    private static final String API_ENDPOINT = "https://api.example.com/productType"; // ✅ Replace with actual API URL

    public static String fetchProductTypeFromAPI(String poId) {
        try {
            URL url = new URL(API_ENDPOINT + "?poId=" + poId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                logger.error("❌ API call failed: HTTP error code " + conn.getResponseCode());
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
            logger.error("❌ Error calling external API: " + e.getMessage());
            return "{\"productType\": \"UNKNOWN\"}";
        }
    }
}