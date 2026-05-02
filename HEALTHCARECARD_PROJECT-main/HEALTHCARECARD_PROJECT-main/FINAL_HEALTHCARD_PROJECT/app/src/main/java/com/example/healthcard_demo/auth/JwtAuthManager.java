package com.example.healthcard_demo.auth;

import android.content.Context;
import android.util.Log;

/**
 * Orchestrates JWT authentication flows for the UI layer.
 * <p>
 * This manager is intentionally best-effort: failures are logged but do not
 * affect the existing local database-based login behavior. All operations
 * run off the main thread so they do not block the UI.
 */
public class JwtAuthManager {

    private static final String TAG = "JwtAuthManager";

    private final JwtAuthClient authClient;
    private final JwtTokenStore tokenStore;

    public JwtAuthManager(Context context) {
        this.authClient = new JwtAuthClient();
        this.tokenStore = new JwtTokenStore(context);
    }

    /**
     * Best-effort background authentication for patients.
     * Existing login behavior is unchanged; this only tries to obtain
     * and persist a JWT for future API calls.
     */
    public void authenticatePatientAsync(final String medicalId, final String password) {
        if (medicalId == null || password == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JwtLoginResult result = authClient.loginPatient(medicalId, password);
                if (result.isSuccess() && result.getToken() != null) {
                    tokenStore.savePatientToken(result.getToken(), result.getExpiresAtMillis());
                    Log.d(TAG, "Saved patient JWT token");
                } else {
                    Log.d(TAG, "Patient JWT login failed: " + result.getErrorMessage());
                }
            }
        }).start();
    }

    /**
     * Best-effort background authentication for doctors.
     * Existing login behavior is unchanged; this only tries to obtain
     * and persist a JWT for future API calls.
     */
    public void authenticateDoctorAsync(final String doctorIdOrMobile, final String password) {
        if (doctorIdOrMobile == null || password == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JwtLoginResult result = authClient.loginDoctor(doctorIdOrMobile, password);
                if (result.isSuccess() && result.getToken() != null) {
                    tokenStore.saveDoctorToken(result.getToken(), result.getExpiresAtMillis());
                    Log.d(TAG, "Saved doctor JWT token");
                } else {
                    Log.d(TAG, "Doctor JWT login failed: " + result.getErrorMessage());
                }
            }
        }).start();
    }
}

