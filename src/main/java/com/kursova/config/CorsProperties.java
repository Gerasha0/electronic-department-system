package com.kursova.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for CORS (Cross-Origin Resource Sharing)
 */
public class CorsProperties {

    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private boolean allowCredentials;
    private long maxAge;

    /**
     * Default constructor with default values
     */
    public CorsProperties() {
        this.allowedOrigins = new ArrayList<>();
        this.allowedMethods = new ArrayList<>();
        this.allowedHeaders = new ArrayList<>();
        this.allowCredentials = true;
        this.maxAge = 3600L;
    }

    // Getters and Setters

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
}
