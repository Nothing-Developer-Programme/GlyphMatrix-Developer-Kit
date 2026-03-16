# Cricket Score Glyph Toy - API Reference

## Quick Reference

### Classes Overview

| Class | Purpose | Key Methods |
|-------|---------|-------------|
| CricketScoreToyService | Main service managing display | onBind(), displayCurrentMatch() |
| CricketApiClient | Fetches cricket data | getLiveMatches() |
| CricketMatch | Data model for matches | getShortName(), isComplete() |
| CricketScoreConfig | User preferences | getFavoriteTeams(), setBrightness() |
| CricketScoreConfigActivity | Settings UI | saveConfiguration() |

---

## CricketScoreToyService

### Overview
Main Android Service that implements the Glyph Toy functionality. Manages the lifecycle, handles Glyph Button events, and renders frames to the LED matrix.

### Lifecycle Methods

#### `onCreate()`
```java
@Override
public void onCreate()
```
Initialize service components, create handlers, and setup clients.

#### `onBind(Intent intent)`
```java
@Override
public IBinder onBind(Intent intent)
```
Called when toy is selected. Initializes Glyph Matrix and starts score updates.
- **Returns**: Messenger binder for receiving events
- **Calls**: initGlyphMatrix()

#### `onUnbind(Intent intent)`
```java
@Override
public boolean onUnbind(Intent intent)
```
Called when toy is deselected. Cleans up resources.
- **Returns**: false (no rebind needed)
- **Calls**: cleanup()

### Display Methods

#### `displayCurrentMatch()`
```java
private void displayCurrentMatch()
```
Main display method that renders based on current display mode.
- Uses current match from mMatches[mCurrentMatchIndex]
- Switches between display modes

#### `displayScore(CricketMatch match)`
```java
private void displayScore(CricketMatch match)
```
Displays team scores with animated cricket ball.
- **Parameters**: match - The match to display
- **Creates**: Animated ball + scrolling text

#### `displayRunRate(CricketMatch match)`
```java
private void displayRunRate(CricketMatch match)
```
Shows current and required run rates.

#### `displayOvers(CricketMatch match)`
```java
private void displayOvers(CricketMatch match)
```
Shows overs information for both teams.

#### `displayMatchStatus(CricketMatch match)`
```java
private void displayMatchStatus(CricketMatch match)
```
Displays match status message.

### Animation Methods

#### `createCricketBallAnimation()`
```java
private GlyphMatrixObject createCricketBallAnimation()
```
Creates animated cricket ball with bouncing effect.
- **Returns**: GlyphMatrixObject with animation
- Uses sine wave for smooth motion

#### `startScrollAnimation()`
```java
private void startScrollAnimation()
```
Starts timer for text scrolling and animations.
- Updates every ANIMATION_FRAME_DELAY ms
- Increments mAnimationFrame counter

### Event Handlers

#### Glyph Button Events
```java
private final Handler serviceHandler
```
Handles messages from Glyph Button:
- `EVENT_CHANGE` - Long press (cycle display modes)
- `EVENT_AOD` - AOD update (refresh scores)
- `EVENT_ACTION_DOWN` - Button pressed
- `EVENT_ACTION_UP` - Button released

### Constants

```java
private static final int UPDATE_INTERVAL_MS = 30000;  // 30s between API calls
private static final int SCROLL_DELAY_MS = 100;        // Scroll speed
private static final int ANIMATION_FRAME_DELAY = 150;  // Animation frame rate
```

---

## CricketApiClient

### Overview
Handles all API communication with cricket score providers. Implements caching, retry logic, and fallback mechanisms.

### Constructor

```java
public CricketApiClient(Context context)
```
- **Parameters**: context - Application context
- Initializes executor service for async operations

### Main Methods

#### `getLiveMatches(ApiCallback callback)`
```java
public void getLiveMatches(ApiCallback callback)
```
Fetches live cricket matches.
- **Parameters**: callback - Receives results
- **Cache**: Returns cached data if < 15s old
- **Async**: Runs on background thread

#### Callback Interface
```java
public interface ApiCallback {
    void onSuccess(List<CricketMatch> matches);
    void onError(String error);
}
```

### API Methods

#### `fetchFromPrimaryApi()`
```java
private List<CricketMatch> fetchFromPrimaryApi()
```
Fetches from main API (CricketData.org by default).
- **Returns**: List of matches or empty list
- Uses configured API key if available

#### `fetchFromFallbackApi()`
```java
private List<CricketMatch> fetchFromFallbackApi()
```
Called if primary API fails.
- **Returns**: Mock data for testing

#### `makeHttpRequest(String url)`
```java
private String makeHttpRequest(String url) throws Exception
```
Makes HTTP GET request.
- **Parameters**: url - API endpoint
- **Returns**: Response body as string
- **Timeout**: 10s connect, 10s read
- **Throws**: Exception on network errors

### Parsing Methods

#### `parseCricketDataResponse(String response)`
```java
private List<CricketMatch> parseCricketDataResponse(String response)
```
Parses JSON from CricketData.org API.
- **Parameters**: response - JSON string
- **Returns**: List of parsed matches

#### `calculateRunRates(CricketMatch match)`
```java
private void calculateRunRates(CricketMatch match)
```
Calculates current and required run rates.
- **Modifies**: match.currentRunRate, match.requiredRunRate

### Utility Methods

#### `getMockMatches()`
```java
private List<CricketMatch> getMockMatches()
```
Returns hardcoded mock matches for testing.
- **Returns**: 2 sample matches

#### `shutdown()`
```java
public void shutdown()
```
Shuts down executor service.

---

## CricketMatch

### Overview
Data model representing a cricket match.

### Fields

```java
// Match identification
public String id;
public String name;
public String matchType;  // "T20", "ODI", "Test"
public String venue;

// Teams
public String team1;
public String team2;

// Scores
public String score1, score2;
public String wickets1, wickets2;
public String overs1, overs2;

// State
public boolean isLive;
public String status;

// Statistics
public double currentRunRate;
public double requiredRunRate;
```

### Methods

#### `getShortName()`
```java
public String getShortName()
```
Returns abbreviated match name (e.g., "IND vs AUS").
- Uses getTeamAbbreviation() for short forms

#### `getTeamAbbreviation(String teamName)`
```java
private String getTeamAbbreviation(String teamName)
```
Converts team name to 3-letter abbreviation.
- **Examples**: "India" → "IND", "Australia" → "AUS"

#### `isComplete()`
```java
public boolean isComplete()
```
Checks if match has all required data.
- **Returns**: true if team1, team2, score1, overs1 are set

---

## CricketScoreConfig

### Overview
Manages user preferences using SharedPreferences.

### Constructor

```java
public CricketScoreConfig(Context context)
```
- Creates/opens shared preferences file

### Team Management

#### `getFavoriteTeams()`
```java
public List<String> getFavoriteTeams()
```
Gets list of favorite teams.
- **Returns**: List of team names

#### `setFavoriteTeams(List<String> teams)`
```java
public void setFavoriteTeams(List<String> teams)
```
Sets favorite teams list.

#### `addFavoriteTeam(String team)`
```java
public void addFavoriteTeam(String team)
```
Adds a team to favorites (if not already present).

#### `removeFavoriteTeam(String team)`
```java
public void removeFavoriteTeam(String team)
```
Removes a team from favorites.

### API Configuration

#### `getApiKey()` / `setApiKey(String key)`
```java
public String getApiKey()
public void setApiKey(String apiKey)
```
Get/set API key for cricket data provider.

#### `getCustomApiUrl()` / `setCustomApiUrl(String url)`
```java
public String getCustomApiUrl()
public void setCustomApiUrl(String url)
```
Get/set custom API endpoint URL.

### Display Settings

#### `getUpdateInterval()` / `setUpdateInterval(int seconds)`
```java
public int getUpdateInterval()
public void setUpdateInterval(int seconds)
```
Get/set update interval (10-120 seconds).
- **Default**: 30 seconds

#### `getBrightness()` / `setBrightness(int brightness)`
```java
public int getBrightness()
public void setBrightness(int brightness)
```
Get/set LED brightness (0-255).
- **Default**: 255

#### `isAnimationsEnabled()` / `setAnimationsEnabled(boolean enabled)`
```java
public boolean isAnimationsEnabled()
public void setAnimationsEnabled(boolean enabled)
```
Get/set animation preference.
- **Default**: true

#### `getScrollSpeed()` / `setScrollSpeed(int speedMs)`
```java
public int getScrollSpeed()
public void setScrollSpeed(int speedMs)
```
Get/set text scroll speed in milliseconds.
- **Default**: 100ms

### Utility Methods

#### `resetToDefaults()`
```java
public void resetToDefaults()
```
Resets all preferences to default values.

#### `getPopularTeams()` (static)
```java
public static List<String> getPopularTeams()
```
Returns list of popular international cricket teams.
- **Returns**: 13 major teams

---

## CricketScoreConfigActivity

### Overview
Configuration UI for user preferences. Activity for setting up favorite teams, API settings, and display options.

### Lifecycle Methods

#### `onCreate(Bundle savedInstanceState)`
```java
@Override
protected void onCreate(Bundle savedInstanceState)
```
Initializes UI, loads current config, sets up listeners.

### UI Methods

#### `initializeViews()`
```java
private void initializeViews()
```
Finds all views from layout XML.

#### `createTeamCheckboxes()`
```java
private void createTeamCheckboxes()
```
Dynamically creates checkboxes for popular teams.

#### `loadCurrentConfig()`
```java
private void loadCurrentConfig()
```
Loads saved preferences into UI controls.

#### `setupListeners()`
```java
private void setupListeners()
```
Attaches event listeners to buttons and controls.

### Action Methods

#### `saveConfiguration()`
```java
private void saveConfiguration()
```
Saves all settings to SharedPreferences.

#### `testConfiguration()`
```java
private void testConfiguration()
```
Tests API connection and shows result.

#### `navigateToGlyphToysManager()`
```java
private void navigateToGlyphToysManager()
```
Opens system Glyph Toys Manager.
- Implements GDK best practice

---

## Display Modes Enum

```java
private enum DisplayMode {
    SCORE,         // Current score with animation
    RUN_RATE,      // Current and required run rate
    OVERS,         // Overs for both teams
    MATCH_STATUS   // Match status message
}
```

Cycled with long press on Glyph Button.

---

## Usage Examples

### Basic Service Usage

```java
// Service is automatically bound when toy is selected
// No manual initialization needed
```

### API Client Usage

```java
CricketApiClient client = new CricketApiClient(context);
client.getLiveMatches(new CricketApiClient.ApiCallback() {
    @Override
    public void onSuccess(List<CricketMatch> matches) {
        // Handle matches
        for (CricketMatch match : matches) {
            Log.d(TAG, match.getShortName());
        }
    }

    @Override
    public void onError(String error) {
        // Handle error
        Log.e(TAG, "Error: " + error);
    }
});
```

### Configuration Usage

```java
CricketScoreConfig config = new CricketScoreConfig(context);

// Add favorite team
config.addFavoriteTeam("India");

// Set brightness
config.setBrightness(200);

// Get settings
int interval = config.getUpdateInterval();
List<String> teams = config.getFavoriteTeams();
```

### Creating Custom Display

```java
private void displayCustom(CricketMatch match) {
    // Create text object
    GlyphMatrixObject textObj = new GlyphMatrixObject.Builder()
            .setText("Your custom text")
            .setPosition(5, 12)
            .setBrightness(config.getBrightness())
            .build();

    // Build frame
    GlyphMatrixFrame frame = new GlyphMatrixFrame.Builder()
            .addTop(textObj)
            .build();

    // Render
    mGlyphManager.setMatrixFrame(frame.render());
}
```

---

## Threading Model

- **Main Thread**: UI operations, GlyphMatrix rendering
- **Background Thread**: API calls (ExecutorService)
- **Timer Threads**: Animations, scroll updates

### Thread Safety

- Use `Handler.post()` to update UI from background threads
- API client manages its own executor
- Config uses thread-safe SharedPreferences

---

## Error Handling

### API Errors
- Network failures: Retry with exponential backoff
- Empty response: Use mock data
- Parse errors: Log and continue

### Display Errors
- No matches: Show "No matches" message
- No favorite teams playing: Show appropriate message
- Glyph service disconnected: Log warning

---

## Performance Considerations

### Memory
- Single instance services
- Cleanup in onUnbind()
- Cancel timers properly

### Network
- Cache for 15 seconds
- Configurable update intervals
- Use WiFi when possible

### Battery
- Reasonable update intervals (30s+)
- Efficient rendering
- Proper cleanup

---

## Extension Points

### Adding New Display Mode
1. Add to DisplayMode enum
2. Create display method
3. Add case in displayCurrentMatch()

### Adding New API Provider
1. Add constants for API URL
2. Implement parsing method
3. Add to fetchFromFallbackApi()

### Custom Animation
1. Override createCricketBallAnimation()
2. Adjust ANIMATION_FRAME_DELAY
3. Implement custom motion logic

---

## Constants Reference

```java
// Update intervals
UPDATE_INTERVAL_MS = 30000    // API fetch interval
SCROLL_DELAY_MS = 100         // Scroll speed
ANIMATION_FRAME_DELAY = 150   // Animation frame rate

// Cache
CACHE_DURATION_MS = 15000     // API cache duration

// Defaults
DEFAULT_UPDATE_INTERVAL = 30  // seconds
DEFAULT_BRIGHTNESS = 255      // max brightness
DEFAULT_SCROLL_SPEED = 100    // milliseconds
DEFAULT_SHOW_ANIMATIONS = true
```

---

## Related Documentation

- [README.md](README.md) - Overview and features
- [SETUP_GUIDE.md](SETUP_GUIDE.md) - Installation instructions
- [Main GDK README](../README.md) - Glyph Matrix Developer Kit

---

*Last updated: 2025*
