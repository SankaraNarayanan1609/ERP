package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Utils.Logger.APIRequestLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ApiClient {
    private final WebDriver driver;
    private String lastProductType;

    public ApiClient(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Sends a POST with JSON, logs request/response, and on non-2xx
     * uses ErrorLogger to capture the root cause.
     * Also parses the returned JSON for a "productType" field.
     *
     * @return raw response body
     */
    public String postJson(String endpoint, String payload, Map<String,String> headers) {
        APIRequestLogger.logRequest("POST", endpoint, headers, payload);

        long start = System.currentTimeMillis();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // set headers
            for (var entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // write body
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
            }

            int status = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    status < 400 ? conn.getInputStream() : conn.getErrorStream()
            ));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                body.append(line).append("\n");
            }
            in.close();

            long timeMs = System.currentTimeMillis() - start;
            APIRequestLogger.logResponse(status, body.toString(), timeMs,
                    "POST " + endpoint, driver);

            // HTTP‐level failure → exception (already logged)
            if (status / 100 != 2) {
                throw new RuntimeException(
                        "HTTP " + status + " on POST " + endpoint);
            }

            // ─── parse out productType ─────────────────────────────
            String rawBody = body.toString();
            try {
                JSONObject json = new JSONObject(rawBody);
                // optString returns null if absent
                lastProductType = json.optString("productType", null);
                APIRequestLogger.logRequest("INFO",
                        "Extracted productType: " + lastProductType, Map.of(), null);
            } catch (Exception je) {
                ErrorLogger.logException(je, "Parsing productType", driver);
            }

            return rawBody;
        } catch (Exception e) {
            // network I/O, JSON parse error, etc.
            ErrorLogger.logException(e, "POST " + endpoint, driver);
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * After calling postJson(), this returns the last seen productType
     * (or null if none was present / parse failed).
     */
    public String getLastProductType() {
        return lastProductType;
    }
}