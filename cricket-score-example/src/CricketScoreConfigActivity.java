package com.example.cricketglyph;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Configuration Activity for Cricket Score Glyph Toy
 *
 * Allows users to:
 * - Select favorite teams
 * - Configure API settings
 * - Adjust display preferences
 * - Test the toy
 * - Navigate to Glyph Toys Manager
 */
public class CricketScoreConfigActivity extends Activity {

    private CricketScoreConfig mConfig;

    private LinearLayout mTeamsContainer;
    private EditText mApiKeyInput;
    private EditText mCustomApiInput;
    private SeekBar mBrightnessSeeker;
    private SeekBar mUpdateIntervalSeeker;
    private CheckBox mAnimationsCheckbox;
    private TextView mBrightnessValue;
    private TextView mUpdateIntervalValue;
    private Button mSaveButton;
    private Button mTestButton;
    private Button mActivateToyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note: You would create a proper XML layout in res/layout/activity_cricket_config.xml
        // For this example, we're showing the code structure

        mConfig = new CricketScoreConfig(this);

        // Initialize views (assumes XML layout is created)
        initializeViews();
        loadCurrentConfig();
        setupListeners();
    }

    /**
     * Initialize all views
     */
    private void initializeViews() {
        // Find views from layout
        // mTeamsContainer = findViewById(R.id.teams_container);
        // mApiKeyInput = findViewById(R.id.api_key_input);
        // mCustomApiInput = findViewById(R.id.custom_api_input);
        // mBrightnessSeeker = findViewById(R.id.brightness_seeker);
        // mUpdateIntervalSeeker = findViewById(R.id.update_interval_seeker);
        // mAnimationsCheckbox = findViewById(R.id.animations_checkbox);
        // mBrightnessValue = findViewById(R.id.brightness_value);
        // mUpdateIntervalValue = findViewById(R.id.update_interval_value);
        // mSaveButton = findViewById(R.id.save_button);
        // mTestButton = findViewById(R.id.test_button);
        // mActivateToyButton = findViewById(R.id.activate_toy_button);

        // Create team selection checkboxes dynamically
        createTeamCheckboxes();
    }

    /**
     * Create checkboxes for popular teams
     */
    private void createTeamCheckboxes() {
        if (mTeamsContainer == null) return;

        List<String> popularTeams = CricketScoreConfig.getPopularTeams();
        List<String> favoriteTeams = mConfig.getFavoriteTeams();

        for (String team : popularTeams) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(team);
            checkbox.setChecked(favoriteTeams.contains(team));
            checkbox.setTag(team);

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String teamName = (String) buttonView.getTag();
                if (isChecked) {
                    mConfig.addFavoriteTeam(teamName);
                } else {
                    mConfig.removeFavoriteTeam(teamName);
                }
            });

            mTeamsContainer.addView(checkbox);
        }
    }

    /**
     * Load current configuration
     */
    private void loadCurrentConfig() {
        // Load API settings
        if (mApiKeyInput != null) {
            String apiKey = mConfig.getApiKey();
            if (apiKey != null) {
                mApiKeyInput.setText(apiKey);
            }
        }

        if (mCustomApiInput != null) {
            String customApi = mConfig.getCustomApiUrl();
            if (customApi != null) {
                mCustomApiInput.setText(customApi);
            }
        }

        // Load display settings
        if (mBrightnessSeeker != null) {
            mBrightnessSeeker.setMax(255);
            mBrightnessSeeker.setProgress(mConfig.getBrightness());
            updateBrightnessLabel(mConfig.getBrightness());
        }

        if (mUpdateIntervalSeeker != null) {
            mUpdateIntervalSeeker.setMax(120); // Max 120 seconds
            mUpdateIntervalSeeker.setMin(10);   // Min 10 seconds
            mUpdateIntervalSeeker.setProgress(mConfig.getUpdateInterval());
            updateIntervalLabel(mConfig.getUpdateInterval());
        }

        if (mAnimationsCheckbox != null) {
            mAnimationsCheckbox.setChecked(mConfig.isAnimationsEnabled());
        }
    }

    /**
     * Setup button and control listeners
     */
    private void setupListeners() {
        // Brightness seeker
        if (mBrightnessSeeker != null) {
            mBrightnessSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateBrightnessLabel(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mConfig.setBrightness(seekBar.getProgress());
                }
            });
        }

        // Update interval seeker
        if (mUpdateIntervalSeeker != null) {
            mUpdateIntervalSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateIntervalLabel(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mConfig.setUpdateInterval(seekBar.getProgress());
                }
            });
        }

        // Animations checkbox
        if (mAnimationsCheckbox != null) {
            mAnimationsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                mConfig.setAnimationsEnabled(isChecked);
            });
        }

        // Save button
        if (mSaveButton != null) {
            mSaveButton.setOnClickListener(v -> saveConfiguration());
        }

        // Test button
        if (mTestButton != null) {
            mTestButton.setOnClickListener(v -> testConfiguration());
        }

        // Activate toy button
        if (mActivateToyButton != null) {
            mActivateToyButton.setOnClickListener(v -> navigateToGlyphToysManager());
        }
    }

    /**
     * Update brightness label
     */
    private void updateBrightnessLabel(int brightness) {
        if (mBrightnessValue != null) {
            mBrightnessValue.setText(String.format("Brightness: %d%%", (brightness * 100) / 255));
        }
    }

    /**
     * Update update interval label
     */
    private void updateIntervalLabel(int seconds) {
        if (mUpdateIntervalValue != null) {
            mUpdateIntervalValue.setText(String.format("Update every: %d seconds", seconds));
        }
    }

    /**
     * Save configuration
     */
    private void saveConfiguration() {
        // Save API settings
        if (mApiKeyInput != null) {
            String apiKey = mApiKeyInput.getText().toString().trim();
            if (!apiKey.isEmpty()) {
                mConfig.setApiKey(apiKey);
            }
        }

        if (mCustomApiInput != null) {
            String customApi = mCustomApiInput.getText().toString().trim();
            if (!customApi.isEmpty()) {
                mConfig.setCustomApiUrl(customApi);
            }
        }

        Toast.makeText(this, "Configuration saved!", Toast.LENGTH_SHORT).show();

        // Show message about activating the toy
        Toast.makeText(this,
                "Tap 'Activate Toy' to add to Glyph Toys Manager",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Test configuration by fetching scores
     */
    private void testConfiguration() {
        Toast.makeText(this, "Testing API connection...", Toast.LENGTH_SHORT).show();

        CricketApiClient apiClient = new CricketApiClient(this);
        apiClient.getLiveMatches(new CricketApiClient.ApiCallback() {
            @Override
            public void onSuccess(List<CricketMatch> matches) {
                runOnUiThread(() -> {
                    String message = "Found " + matches.size() + " matches!";
                    Toast.makeText(CricketScoreConfigActivity.this,
                            message, Toast.LENGTH_SHORT).show();

                    // Show first match info
                    if (!matches.isEmpty()) {
                        CricketMatch match = matches.get(0);
                        String matchInfo = match.team1 + " vs " + match.team2;
                        Toast.makeText(CricketScoreConfigActivity.this,
                                matchInfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(CricketScoreConfigActivity.this,
                            "Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Navigate to Glyph Toys Manager to activate the toy
     * Best practice recommended in the GDK documentation
     */
    private void navigateToGlyphToysManager() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    "com.nothing.thirdparty",
                    "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity"
            ));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Unable to open Glyph Toys Manager. Please ensure your system is updated.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
