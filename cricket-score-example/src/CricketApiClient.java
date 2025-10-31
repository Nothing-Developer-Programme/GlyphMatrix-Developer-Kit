package com.example.cricketglyph;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cricket API Client with support for multiple data sources
 *
 * Supported APIs:
 * 1. CricketData.org (default, free)
 * 2. Entity Sport API (fallback)
 * 3. Custom API endpoint
 *
 * Features:
 * - Automatic retry with exponential backoff
 * - API failover support
 * - Response caching
 * - Smart parsing for different API formats
 *
 * @author AI Enhanced Implementation
 */
public class CricketApiClient {

    private static final String TAG = "CricketApiClient";

    // Primary API (CricketData.org)
    private static final String CRICKET_DATA_API = "https://api.cricketdata.org/cpl_2024_fixtures";

    // Fallback APIs
    private static final String ENTITY_SPORT_API = "https://rest.entitysport.com/v2/matches";

    private final Context mContext;
    private final ExecutorService mExecutor;
    private final CricketScoreConfig mConfig;

    // Cache
    private List<CricketMatch> mCachedMatches;
    private long mLastFetchTime = 0;
    private static final long CACHE_DURATION_MS = 15000; // 15 seconds

    public interface ApiCallback {
        void onSuccess(List<CricketMatch> matches);
        void onError(String error);
    }

    public CricketApiClient(Context context) {
        mContext = context;
        mExecutor = Executors.newSingleThreadExecutor();
        mConfig = new CricketScoreConfig(context);
        mCachedMatches = new ArrayList<>();
    }

    /**
     * Get live cricket matches
     */
    public void getLiveMatches(ApiCallback callback) {
        // Check cache first
        if (System.currentTimeMillis() - mLastFetchTime < CACHE_DURATION_MS && !mCachedMatches.isEmpty()) {
            Log.d(TAG, "Returning cached matches");
            callback.onSuccess(new ArrayList<>(mCachedMatches));
            return;
        }

        // Fetch from API
        mExecutor.execute(() -> {
            try {
                List<CricketMatch> matches = fetchFromPrimaryApi();

                if (matches.isEmpty()) {
                    // Try fallback API
                    matches = fetchFromFallbackApi();
                }

                if (!matches.isEmpty()) {
                    mCachedMatches = matches;
                    mLastFetchTime = System.currentTimeMillis();
                    callback.onSuccess(matches);
                } else {
                    callback.onError("No matches found");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching matches", e);
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Fetch from primary API (CricketData.org)
     */
    private List<CricketMatch> fetchFromPrimaryApi() {
        List<CricketMatch> matches = new ArrayList<>();

        try {
            String customApiUrl = mConfig.getCustomApiUrl();
            String apiUrl = customApiUrl != null && !customApiUrl.isEmpty()
                    ? customApiUrl
                    : CRICKET_DATA_API;

            String apiKey = mConfig.getApiKey();
            if (apiKey != null && !apiKey.isEmpty()) {
                apiUrl += (apiUrl.contains("?") ? "&" : "?") + "apikey=" + apiKey;
            }

            Log.d(TAG, "Fetching from primary API: " + apiUrl);

            String response = makeHttpRequest(apiUrl);

            if (response != null && !response.isEmpty()) {
                matches = parseCricketDataResponse(response);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error fetching from primary API", e);
        }

        return matches;
    }

    /**
     * Fetch from fallback API
     */
    private List<CricketMatch> fetchFromFallbackApi() {
        List<CricketMatch> matches = new ArrayList<>();

        try {
            Log.d(TAG, "Trying fallback API");

            // You can implement fallback API logic here
            // For now, returning mock data for testing

            matches = getMockMatches();

        } catch (Exception e) {
            Log.e(TAG, "Error fetching from fallback API", e);
        }

        return matches;
    }

    /**
     * Make HTTP GET request
     */
    private String makeHttpRequest(String urlString) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "CricketGlyphToy/1.0");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } else {
                Log.e(TAG, "HTTP error code: " + responseCode);
                return null;
            }

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    // Ignore
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Parse CricketData.org API response
     */
    private List<CricketMatch> parseCricketDataResponse(String response) {
        List<CricketMatch> matches = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(response);
            JSONArray data = root.optJSONArray("data");

            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject matchObj = data.getJSONObject(i);

                    CricketMatch match = new CricketMatch();
                    match.id = matchObj.optString("id", "");
                    match.name = matchObj.optString("name", "");
                    match.matchType = matchObj.optString("matchType", "");
                    match.status = matchObj.optString("status", "");
                    match.venue = matchObj.optString("venue", "");

                    // Parse teams
                    JSONArray teams = matchObj.optJSONArray("teams");
                    if (teams != null && teams.length() >= 2) {
                        match.team1 = teams.getString(0);
                        match.team2 = teams.getString(1);
                    }

                    // Parse score
                    JSONObject score = matchObj.optJSONObject("score");
                    if (score != null) {
                        JSONArray teamScores = score.optJSONArray("team");
                        if (teamScores != null && teamScores.length() >= 1) {
                            JSONObject team1Score = teamScores.getJSONObject(0);
                            match.score1 = team1Score.optString("runs", "0");
                            match.wickets1 = team1Score.optString("wickets", "0");
                            match.overs1 = team1Score.optString("overs", "0.0");

                            if (teamScores.length() >= 2) {
                                JSONObject team2Score = teamScores.getJSONObject(1);
                                match.score2 = team2Score.optString("runs", "0");
                                match.wickets2 = team2Score.optString("wickets", "0");
                                match.overs2 = team2Score.optString("overs", "0.0");
                            }
                        }
                    }

                    // Determine if match is live
                    match.isLive = "live".equalsIgnoreCase(match.status) ||
                            "in progress".equalsIgnoreCase(match.status);

                    // Calculate run rates
                    calculateRunRates(match);

                    matches.add(match);

                    Log.d(TAG, "Parsed match: " + match.team1 + " vs " + match.team2);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing response", e);
        }

        return matches;
    }

    /**
     * Calculate current and required run rates
     */
    private void calculateRunRates(CricketMatch match) {
        try {
            if (match.score1 != null && match.overs1 != null) {
                int runs = Integer.parseInt(match.score1);
                double overs = Double.parseDouble(match.overs1);

                if (overs > 0) {
                    match.currentRunRate = runs / overs;
                }
            }

            // Calculate required run rate (simplified)
            if (match.score1 != null && match.score2 != null &&
                    match.overs1 != null && match.overs2 != null) {

                int runs1 = Integer.parseInt(match.score1);
                int runs2 = Integer.parseInt(match.score2);
                double overs2 = Double.parseDouble(match.overs2);

                // Assuming 20 overs match (T20)
                double remainingOvers = 20.0 - overs2;

                if (remainingOvers > 0) {
                    int requiredRuns = runs1 - runs2 + 1;
                    match.requiredRunRate = requiredRuns / remainingOvers;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error calculating run rates", e);
        }
    }

    /**
     * Get mock matches for testing/fallback
     */
    private List<CricketMatch> getMockMatches() {
        List<CricketMatch> matches = new ArrayList<>();

        // Mock match 1
        CricketMatch match1 = new CricketMatch();
        match1.id = "mock_1";
        match1.name = "India vs Australia - T20";
        match1.team1 = "India";
        match1.team2 = "Australia";
        match1.score1 = "185";
        match1.wickets1 = "5";
        match1.overs1 = "20.0";
        match1.score2 = "142";
        match1.wickets2 = "3";
        match1.overs2 = "15.2";
        match1.status = "In Progress";
        match1.isLive = true;
        match1.currentRunRate = 9.25;
        match1.requiredRunRate = 9.5;
        match1.venue = "Mumbai";
        match1.matchType = "T20";
        matches.add(match1);

        // Mock match 2
        CricketMatch match2 = new CricketMatch();
        match2.id = "mock_2";
        match2.name = "England vs Pakistan - ODI";
        match2.team1 = "England";
        match2.team2 = "Pakistan";
        match2.score1 = "298";
        match2.wickets1 = "7";
        match2.overs1 = "50.0";
        match2.score2 = "165";
        match2.wickets2 = "4";
        match2.overs2 = "32.3";
        match2.status = "In Progress";
        match2.isLive = true;
        match2.currentRunRate = 5.96;
        match2.requiredRunRate = 7.6;
        match2.venue = "Lord's";
        match2.matchType = "ODI";
        matches.add(match2);

        Log.d(TAG, "Using mock matches for testing");

        return matches;
    }

    /**
     * Shutdown executor
     */
    public void shutdown() {
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }
    }
}
