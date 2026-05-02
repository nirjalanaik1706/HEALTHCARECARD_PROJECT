package com.example.healthcard_demo.auth;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Very small HTTP client responsible for exchanging user credentials
 * for JWT tokens with the backend.
 * <p>
 * This client is deliberately simple and defensive: any network or parsing
 * errors are captured and returned as a failed {@link JwtLoginResult}
 * instead of throwing, so callers can safely ignore failures without
 * impacting local database-based login.
 */
public class JwtAuthClient {

    private static final String TAG = "JwtAuthClient";

    public JwtLoginResult loginPatient(String medicalId, String password) {
        String url = JwtConfig.BASE_URL + JwtConfig.PATIENT_LOGIN_PATH;
        return executeLoginRequest(url, medicalId, password);
    }

    public JwtLoginResult loginDoctor(String doctorIdOrMobile, String password) {
        String url = JwtConfig.BASE_URL + JwtConfig.DOCTOR_LOGIN_PATH;
        return executeLoginRequest(url, doctorIdOrMobile, password);
    }

    private JwtLoginResult executeLoginRequest(String urlString, String username, String password) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            JSONObject payload = new JSONObject();
            payload.put("username", username);
            payload.put("password", password);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(payload.toString());
            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();
            InputStream inputStream;
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            String responseBody = readStream(inputStream);
            if (responseCode >= 200 && responseCode < 300) {
                return parseSuccessResponse(responseBody);
            } else {
                String message = "HTTP " + responseCode + " while requesting JWT";
                Log.w(TAG, message + " body=" + responseBody);
                return new JwtLoginResult(false, null, message, 0L);
            }
        } catch (IOException | JSONException e) {
            Log.w(TAG, "Error during JWT login: " + e.getMessage(), e);
            return new JwtLoginResult(false, null, e.getMessage(), 0L);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    private JwtLoginResult parseSuccessResponse(String responseBody) throws JSONException {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new JwtLoginResult(false, null, "Empty response body", 0L);
        }

        JSONObject json = new JSONObject(responseBody);

        // Try several common field names for the token to keep backend flexible.
        String token = null;
        if (json.has(JwtConfig.FIELD_ACCESS_TOKEN)) {
            token = json.optString(JwtConfig.FIELD_ACCESS_TOKEN, null);
        }
        if (token == null && json.has(JwtConfig.FIELD_TOKEN)) {
            token = json.optString(JwtConfig.FIELD_TOKEN, null);
        }
        if (token == null && json.has(JwtConfig.FIELD_JWT)) {
            token = json.optString(JwtConfig.FIELD_JWT, null);
        }

        long expiresAt = 0L;
        if (json.has(JwtConfig.FIELD_EXPIRES_AT)) {
            expiresAt = json.optLong(JwtConfig.FIELD_EXPIRES_AT, 0L);
        }

        if (token == null || token.trim().isEmpty()) {
            return new JwtLoginResult(false, null, "No token in response", 0L);
        }

        return new JwtLoginResult(true, token, null, expiresAt);
    }
}

