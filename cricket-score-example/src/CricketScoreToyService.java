package com.example.cricketglyph;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.GlyphFrame;
import com.nothing.ketchum.GlyphMatrixFrame;
import com.nothing.ketchum.GlyphMatrixManager;
import com.nothing.ketchum.GlyphMatrixObject;
import com.nothing.ketchum.GlyphMatrixUtils;
import com.nothing.ketchum.GlyphToy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Cricket Score Live Glyph Toy Service
 *
 * Features:
 * - Real-time cricket scores for favorite teams
 * - Multiple display modes (score, run rate, overs)
 * - Smooth scrolling text and animations
 * - Auto-refresh with smart caching
 * - AOD support for continuous updates
 * - Long press to cycle through matches
 *
 * @author AI Enhanced Implementation
 */
public class CricketScoreToyService extends Service {

    private static final String TAG = "CricketScoreToy";
    private static final int UPDATE_INTERVAL_MS = 30000; // 30 seconds
    private static final int SCROLL_DELAY_MS = 100;
    private static final int ANIMATION_FRAME_DELAY = 150;

    private GlyphMatrixManager mGlyphManager;
    private GlyphMatrixManager.Callback mCallback;
    private CricketApiClient mApiClient;
    private CricketScoreConfig mConfig;

    private Timer mUpdateTimer;
    private Timer mScrollTimer;
    private Handler mMainHandler;

    private List<CricketMatch> mMatches;
    private int mCurrentMatchIndex = 0;
    private int mScrollPosition = 0;
    private DisplayMode mCurrentMode = DisplayMode.SCORE;

    private boolean mIsAnimating = false;
    private int mAnimationFrame = 0;

    // Display modes
    private enum DisplayMode {
        SCORE,          // Shows current score
        RUN_RATE,       // Shows run rate and required rate
        OVERS,          // Shows overs remaining
        MATCH_STATUS    // Shows match status (live/upcoming/finished)
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMainHandler = new Handler(Looper.getMainLooper());
        mApiClient = new CricketApiClient(this);
        mConfig = new CricketScoreConfig(this);
        mMatches = new ArrayList<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound - Starting cricket score display");
        initGlyphMatrix();
        return serviceMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service unbound - Cleaning up");
        cleanup();
        return false;
    }

    /**
     * Initialize Glyph Matrix Manager and start fetching scores
     */
    private void initGlyphMatrix() {
        mGlyphManager = GlyphMatrixManager.getInstance(getApplicationContext());
        mCallback = new GlyphMatrixManager.Callback() {
            @Override
            public void onServiceConnected(ComponentName componentName) {
                Log.d(TAG, "Glyph Matrix service connected");
                mGlyphManager.register(Common.DEVICE_23112);
                startCricketUpdates();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.w(TAG, "Glyph Matrix service disconnected");
            }
        };
        mGlyphManager.init(mCallback);
    }

    /**
     * Start fetching and displaying cricket scores
     */
    private void startCricketUpdates() {
        // Fetch initial data
        fetchCricketScores();

        // Schedule periodic updates
        mUpdateTimer = new Timer();
        mUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fetchCricketScores();
            }
        }, UPDATE_INTERVAL_MS, UPDATE_INTERVAL_MS);

        // Start scrolling animation
        startScrollAnimation();
    }

    /**
     * Fetch cricket scores from API
     */
    private void fetchCricketScores() {
        mApiClient.getLiveMatches(new CricketApiClient.ApiCallback() {
            @Override
            public void onSuccess(List<CricketMatch> matches) {
                mMainHandler.post(() -> {
                    updateMatches(matches);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error fetching scores: " + error);
                mMainHandler.post(() -> {
                    displayError(error);
                });
            }
        });
    }

    /**
     * Update matches and filter by favorite teams
     */
    private void updateMatches(List<CricketMatch> matches) {
        List<String> favoriteTeams = mConfig.getFavoriteTeams();

        if (favoriteTeams.isEmpty()) {
            // No favorite teams set, show all live matches
            mMatches = matches;
        } else {
            // Filter matches by favorite teams
            mMatches.clear();
            for (CricketMatch match : matches) {
                if (isFavoriteTeamPlaying(match, favoriteTeams)) {
                    mMatches.add(match);
                }
            }
        }

        // Reset to first match if current index is out of bounds
        if (mCurrentMatchIndex >= mMatches.size()) {
            mCurrentMatchIndex = 0;
        }

        Log.d(TAG, "Updated matches: " + mMatches.size() + " matches found");

        // Display current match
        if (!mMatches.isEmpty()) {
            displayCurrentMatch();
        } else {
            displayNoMatches();
        }
    }

    /**
     * Check if any favorite team is playing in the match
     */
    private boolean isFavoriteTeamPlaying(CricketMatch match, List<String> favoriteTeams) {
        for (String team : favoriteTeams) {
            if (match.team1.toLowerCase().contains(team.toLowerCase()) ||
                match.team2.toLowerCase().contains(team.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Display current match based on display mode
     */
    private void displayCurrentMatch() {
        if (mMatches.isEmpty()) return;

        CricketMatch match = mMatches.get(mCurrentMatchIndex);

        switch (mCurrentMode) {
            case SCORE:
                displayScore(match);
                break;
            case RUN_RATE:
                displayRunRate(match);
                break;
            case OVERS:
                displayOvers(match);
                break;
            case MATCH_STATUS:
                displayMatchStatus(match);
                break;
        }
    }

    /**
     * Display score with animated cricket ball
     */
    private void displayScore(CricketMatch match) {
        String scoreText = formatScoreText(match);

        // Create animated cricket ball icon
        GlyphMatrixObject cricketBall = createCricketBallAnimation();

        // Create scrolling score text
        GlyphMatrixObject scoreObject = new GlyphMatrixObject.Builder()
                .setText(scoreText)
                .setPosition(8, 12)
                .setBrightness(255)
                .build();

        // Build and render frame
        GlyphMatrixFrame frame = new GlyphMatrixFrame.Builder()
                .addTop(cricketBall)
                .addMid(scoreObject)
                .build();

        mGlyphManager.setMatrixFrame(frame.render());
    }

    /**
     * Create animated cricket ball that bounces
     */
    private GlyphMatrixObject createCricketBallAnimation() {
        // Simple ball animation using circular movement
        int yOffset = (int) (Math.sin(mAnimationFrame * 0.3) * 3);

        GlyphMatrixObject.Builder ballBuilder = new GlyphMatrixObject.Builder();

        // You would replace this with actual cricket ball bitmap from resources
        // For now, using a simple circle representation
        Bitmap ballBitmap = createCircleBitmap(5, 255);

        return ballBuilder
                .setImageSource(ballBitmap)
                .setPosition(2, 10 + yOffset)
                .setBrightness(255)
                .setScale(80)
                .build();
    }

    /**
     * Create a simple circle bitmap for cricket ball
     */
    private Bitmap createCircleBitmap(int radius, int brightness) {
        int size = radius * 2 + 1;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        // Simple circle drawing (you'd want to use Canvas for production)
        int[] pixels = new int[size * size];
        int centerX = radius;
        int centerY = radius;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int dx = x - centerX;
                int dy = y - centerY;
                if (dx * dx + dy * dy <= radius * radius) {
                    pixels[y * size + x] = 0xFF000000 | (brightness << 16) | (brightness << 8) | brightness;
                }
            }
        }

        bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
        return bitmap;
    }

    /**
     * Format score text for display
     */
    private String formatScoreText(CricketMatch match) {
        StringBuilder sb = new StringBuilder();

        if (match.isLive) {
            sb.append("🔴 LIVE: ");
        }

        sb.append(match.team1).append(" ");
        sb.append(match.score1).append("/").append(match.wickets1);
        sb.append(" (").append(match.overs1).append(") ");

        if (match.score2 != null && !match.score2.isEmpty()) {
            sb.append("vs ").append(match.team2).append(" ");
            sb.append(match.score2).append("/").append(match.wickets2);
            sb.append(" (").append(match.overs2).append(")");
        } else {
            sb.append("vs ").append(match.team2);
        }

        if (match.status != null && !match.status.isEmpty()) {
            sb.append(" - ").append(match.status);
        }

        return sb.toString();
    }

    /**
     * Display run rate information
     */
    private void displayRunRate(CricketMatch match) {
        String rrText = String.format("CRR: %.2f | RRR: %.2f",
                match.currentRunRate, match.requiredRunRate);

        GlyphMatrixObject textObject = new GlyphMatrixObject.Builder()
                .setText(rrText)
                .setPosition(0, 12)
                .setBrightness(240)
                .build();

        GlyphMatrixFrame frame = new GlyphMatrixFrame.Builder()
                .addTop(textObject)
                .build();

        mGlyphManager.setMatrixFrame(frame.render());
    }

    /**
     * Display overs information
     */
    private void displayOvers(CricketMatch match) {
        String oversText = String.format("%s: %s | %s: %s",
                match.team1, match.overs1, match.team2, match.overs2);

        GlyphMatrixObject textObject = new GlyphMatrixObject.Builder()
                .setText(oversText)
                .setPosition(0, 12)
                .setBrightness(240)
                .build();

        GlyphMatrixFrame frame = new GlyphMatrixFrame.Builder()
                .addTop(textObject)
                .build();

        mGlyphManager.setMatrixFrame(frame.render());
    }

    /**
     * Display match status
     */
    private void displayMatchStatus(CricketMatch match) {
        String statusText = match.status != null ? match.status : "Match in progress";

        GlyphMatrixObject textObject = new GlyphMatrixObject.Builder()
                .setText(statusText)
                .setPosition(0, 12)
                .setBrightness(255)
                .build();

        GlyphMatrixFrame frame = new GlyphMatrixFrame.Builder()
                .addTop(textObject)
                .build();

        mGlyphManager.setMatrixFrame(frame.render());
    }

    /**
     * Display error message
     */
    private void displayError(String error) {
        GlyphMatrixObject errorObject = new GlyphMatrixObject.Builder()
                .setText("⚠️ " + error)
                .setPosition(0, 12)
                .setBrightness(200)
                .build();

        GlyphMatrixFrame frame = new GlyphMatrixFrame.Builder()
                .addTop(errorObject)
                .build();

        mGlyphManager.setMatrixFrame(frame.render());
    }

    /**
     * Display "no matches" message
     */
    private void displayNoMatches() {
        String message = mConfig.getFavoriteTeams().isEmpty()
                ? "No live matches"
                : "No matches for your favorite teams";

        GlyphMatrixObject textObject = new GlyphMatrixObject.Builder()
                .setText(message)
                .setPosition(0, 12)
                .setBrightness(200)
                .build();

        GlyphMatrixFrame frame = new GlyphMatrixFrame.Builder()
                .addTop(textObject)
                .build();

        mGlyphManager.setMatrixFrame(frame.render());
    }

    /**
     * Start scrolling animation for long text
     */
    private void startScrollAnimation() {
        mScrollTimer = new Timer();
        mScrollTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mMainHandler.post(() -> {
                    mScrollPosition++;
                    mAnimationFrame++;
                    displayCurrentMatch();
                });
            }
        }, ANIMATION_FRAME_DELAY, ANIMATION_FRAME_DELAY);
    }

    /**
     * Handle Glyph Button events
     */
    private final Handler serviceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlyphToy.MSG_GLYPH_TOY: {
                    Bundle bundle = msg.getData();
                    String event = bundle.getString(GlyphToy.MSG_GLYPH_TOY_DATA);

                    if (GlyphToy.EVENT_CHANGE.equals(event)) {
                        // Long press: cycle through display modes
                        handleLongPress();
                    } else if (GlyphToy.EVENT_AOD.equals(event)) {
                        // AOD update: refresh scores
                        fetchCricketScores();
                    } else if (GlyphToy.EVENT_ACTION_DOWN.equals(event)) {
                        // Button pressed
                        handleButtonDown();
                    } else if (GlyphToy.EVENT_ACTION_UP.equals(event)) {
                        // Button released
                        handleButtonUp();
                    }
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private final Messenger serviceMessenger = new Messenger(serviceHandler);

    /**
     * Handle long press event - cycle through display modes
     */
    private void handleLongPress() {
        if (mMatches.isEmpty()) {
            // No matches, cycle through matches if available
            mCurrentMatchIndex = (mCurrentMatchIndex + 1) % Math.max(1, mMatches.size());
        } else {
            // Cycle through display modes
            DisplayMode[] modes = DisplayMode.values();
            int currentIndex = mCurrentMode.ordinal();
            mCurrentMode = modes[(currentIndex + 1) % modes.length];

            Log.d(TAG, "Switched to display mode: " + mCurrentMode);
        }

        displayCurrentMatch();
    }

    /**
     * Handle button down event
     */
    private void handleButtonDown() {
        // Could add visual feedback here
    }

    /**
     * Handle button up event
     */
    private void handleButtonUp() {
        // Could add action here
    }

    /**
     * Clean up resources
     */
    private void cleanup() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mScrollTimer != null) {
            mScrollTimer.cancel();
            mScrollTimer = null;
        }

        if (mGlyphManager != null) {
            mGlyphManager.turnOff();
            mGlyphManager.unInit();
            mGlyphManager = null;
        }

        mCallback = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanup();
    }
}
