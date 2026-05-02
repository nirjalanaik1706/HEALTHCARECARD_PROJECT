package com.example.healthcard_demo.auth;

/**
 * Simple DTO that represents the result of a JWT login attempt.
 * <p>
 * This object is only used inside the authentication layer and does not
 * affect existing UI contracts. It is designed so that failures can be
 * safely ignored by callers that only care about best-effort JWT retrieval.
 */
public class JwtLoginResult {

    private final boolean success;
    private final String token;
    private final String errorMessage;
    private final long expiresAtMillis;

    public JwtLoginResult(boolean success, String token, String errorMessage, long expiresAtMillis) {
        this.success = success;
        this.token = token;
        this.errorMessage = errorMessage;
        this.expiresAtMillis = expiresAtMillis;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }
}

