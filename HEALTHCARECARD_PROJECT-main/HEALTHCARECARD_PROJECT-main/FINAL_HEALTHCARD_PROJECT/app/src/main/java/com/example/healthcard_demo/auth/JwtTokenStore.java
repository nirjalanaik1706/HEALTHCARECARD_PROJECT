package com.example.healthcard_demo.auth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple SharedPreferences-backed storage for JWT tokens.
 * <p>
 * This class is used by the authentication layer to persist tokens for
 * later use by API clients. It does not affect existing database-based
 * authentication and can be safely introduced without behavior changes.
 */
public class JwtTokenStore {
    private static final String PREFS_NAME = "jwt_prefs";
    private static final String KEY_PATIENT_TOKEN = "patient_jwt_token";
    private static final String KEY_DOCTOR_TOKEN = "doctor_jwt_token";
    private static final String KEY_PATIENT_EXPIRES_AT = "patient_expires_at";
    private static final String KEY_DOCTOR_EXPIRES_AT = "doctor_expires_at";

    private final SharedPreferences prefs;

    public JwtTokenStore(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void savePatientToken(String token, long expiresAtMillis) {
        prefs.edit()
                .putString(KEY_PATIENT_TOKEN, token)
                .putLong(KEY_PATIENT_EXPIRES_AT, expiresAtMillis)
                .apply();
    }

    public void saveDoctorToken(String token, long expiresAtMillis) {
        prefs.edit()
                .putString(KEY_DOCTOR_TOKEN, token)
                .putLong(KEY_DOCTOR_EXPIRES_AT, expiresAtMillis)
                .apply();
    }

    public String getPatientToken() {
        return prefs.getString(KEY_PATIENT_TOKEN, null);
    }

    public String getDoctorToken() {
        return prefs.getString(KEY_DOCTOR_TOKEN, null);
    }
    // 

    public long getPatientTokenExpiresAt() {
        return prefs.getLong(KEY_PATIENT_EXPIRES_AT, 0L);
    }

    public long getDoctorTokenExpiresAt() {
        return prefs.getLong(KEY_DOCTOR_EXPIRES_AT, 0L);
    }

    public void clearAllTokens() {
        prefs.edit().clear().apply();
    }
}

