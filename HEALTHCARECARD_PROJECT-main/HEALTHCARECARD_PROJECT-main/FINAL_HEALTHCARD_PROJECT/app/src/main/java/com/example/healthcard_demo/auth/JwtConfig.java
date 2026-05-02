package com.example.healthcard_demo.auth;

/**
 * Central configuration for JWT authentication integration.
 * <p>
 * This class is intentionally simple and self-contained so that
 * changing backend URLs or paths does not require touching UI code.
 *
 * NOTE: All values here are placeholders and can be adjusted
 * to match the actual backend implementation without breaking
 * existing login behavior.
 */
public final class JwtConfig {

    private JwtConfig() {
        // no-op
    }

    // Base URL for the backend API that issues JWTs.
    // Replace with your real server URL when available.
    public static final String BASE_URL = "https://example.com/api/";

    // Endpoint paths (appended to BASE_URL).
    public static final String PATIENT_LOGIN_PATH = "auth/patient/login";
    public static final String DOCTOR_LOGIN_PATH = "auth/doctor/login";

    // HTTP / JWT related constants.
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // Expected JSON fields in the login response.
    public static final String FIELD_ACCESS_TOKEN = "access_token";
    public static final String FIELD_TOKEN = "token";
    public static final String FIELD_JWT = "jwt";
    public static final String FIELD_EXPIRES_AT = "expires_at";
}

