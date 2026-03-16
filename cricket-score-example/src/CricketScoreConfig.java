package com.example.cricketglyph;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Configuration manager for Cricket Score Glyph Toy
 *
 * Manages user preferences such as:
 * - Favorite teams
 * - API keys
 * - Custom API URLs
 * - Update intervals
 * - Display preferences
 */
public class CricketScoreConfig {

    private static final String PREFS_NAME = "cricket_score_prefs";

    // Preference keys
    private static final String KEY_FAVORITE_TEAMS = "favorite_teams";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_CUSTOM_API_URL = "custom_api_url";
    private static final String KEY_UPDATE_INTERVAL = "update_interval";
    private static final String KEY_SHOW_ANIMATIONS = "show_animations";
    private static final String KEY_BRIGHTNESS = "brightness";
    private static final String KEY_SCROLL_SPEED = "scroll_speed";

    // Default values
    private static final int DEFAULT_UPDATE_INTERVAL = 30; // seconds
    private static final boolean DEFAULT_SHOW_ANIMATIONS = true;
    private static final int DEFAULT_BRIGHTNESS = 255;
    private static final int DEFAULT_SCROLL_SPEED = 100; // ms

    private final SharedPreferences mPrefs;

    public CricketScoreConfig(Context context) {
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get list of favorite teams
     */
    public List<String> getFavoriteTeams() {
        Set<String> teams = mPrefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>());
        return new ArrayList<>(teams);
    }

    /**
     * Set favorite teams
     */
    public void setFavoriteTeams(List<String> teams) {
        Set<String> teamSet = new HashSet<>(teams);
        mPrefs.edit().putStringSet(KEY_FAVORITE_TEAMS, teamSet).apply();
    }

    /**
     * Add a favorite team
     */
    public void addFavoriteTeam(String team) {
        List<String> teams = getFavoriteTeams();
        if (!teams.contains(team)) {
            teams.add(team);
            setFavoriteTeams(teams);
        }
    }

    /**
     * Remove a favorite team
     */
    public void removeFavoriteTeam(String team) {
        List<String> teams = getFavoriteTeams();
        teams.remove(team);
        setFavoriteTeams(teams);
    }

    /**
     * Get API key
     */
    public String getApiKey() {
        return mPrefs.getString(KEY_API_KEY, null);
    }

    /**
     * Set API key
     */
    public void setApiKey(String apiKey) {
        mPrefs.edit().putString(KEY_API_KEY, apiKey).apply();
    }

    /**
     * Get custom API URL
     */
    public String getCustomApiUrl() {
        return mPrefs.getString(KEY_CUSTOM_API_URL, null);
    }

    /**
     * Set custom API URL
     */
    public void setCustomApiUrl(String url) {
        mPrefs.edit().putString(KEY_CUSTOM_API_URL, url).apply();
    }

    /**
     * Get update interval in seconds
     */
    public int getUpdateInterval() {
        return mPrefs.getInt(KEY_UPDATE_INTERVAL, DEFAULT_UPDATE_INTERVAL);
    }

    /**
     * Set update interval in seconds
     */
    public void setUpdateInterval(int seconds) {
        mPrefs.edit().putInt(KEY_UPDATE_INTERVAL, seconds).apply();
    }

    /**
     * Check if animations are enabled
     */
    public boolean isAnimationsEnabled() {
        return mPrefs.getBoolean(KEY_SHOW_ANIMATIONS, DEFAULT_SHOW_ANIMATIONS);
    }

    /**
     * Enable/disable animations
     */
    public void setAnimationsEnabled(boolean enabled) {
        mPrefs.edit().putBoolean(KEY_SHOW_ANIMATIONS, enabled).apply();
    }

    /**
     * Get display brightness (0-255)
     */
    public int getBrightness() {
        return mPrefs.getInt(KEY_BRIGHTNESS, DEFAULT_BRIGHTNESS);
    }

    /**
     * Set display brightness (0-255)
     */
    public void setBrightness(int brightness) {
        brightness = Math.max(0, Math.min(255, brightness));
        mPrefs.edit().putInt(KEY_BRIGHTNESS, brightness).apply();
    }

    /**
     * Get scroll speed in milliseconds
     */
    public int getScrollSpeed() {
        return mPrefs.getInt(KEY_SCROLL_SPEED, DEFAULT_SCROLL_SPEED);
    }

    /**
     * Set scroll speed in milliseconds
     */
    public void setScrollSpeed(int speedMs) {
        mPrefs.edit().putInt(KEY_SCROLL_SPEED, speedMs).apply();
    }

    /**
     * Reset all preferences to defaults
     */
    public void resetToDefaults() {
        mPrefs.edit().clear().apply();
    }

    /**
     * Get all popular cricket teams for quick selection
     */
    public static List<String> getPopularTeams() {
        return Arrays.asList(
            "India",
            "Australia",
            "England",
            "Pakistan",
            "South Africa",
            "New Zealand",
            "Sri Lanka",
            "West Indies",
            "Bangladesh",
            "Afghanistan",
            "Zimbabwe",
            "Ireland",
            "Netherlands"
        );
    }
}
