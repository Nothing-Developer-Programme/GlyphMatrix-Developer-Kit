# Cricket Score Glyph Toy - Setup Guide

## Step-by-Step Installation

### 1. Project Setup

#### Create a New Android Project
```bash
# In Android Studio
File → New → New Project
Select "Empty Activity"
Name: CricketScoreGlyph
Package: com.example.cricketglyph
Minimum SDK: API 29 (Android 10)
```

#### Project Structure
```
app/
├── src/
│   └── main/
│       ├── java/com/example/cricketglyph/
│       │   ├── CricketScoreToyService.java
│       │   ├── CricketApiClient.java
│       │   ├── CricketMatch.java
│       │   ├── CricketScoreConfig.java
│       │   └── CricketScoreConfigActivity.java
│       ├── res/
│       │   ├── values/
│       │   │   └── strings.xml
│       │   ├── drawable/
│       │   │   └── cricket_score_preview.xml (vector asset)
│       │   └── layout/
│       │       └── activity_cricket_config.xml
│       └── AndroidManifest.xml
└── build.gradle
```

### 2. Copy Files

#### Copy SDK
```bash
# Create libs directory
mkdir -p app/libs

# Copy the Glyph Matrix SDK
cp /path/to/glyph-matrix-sdk-1.0.aar app/libs/
```

#### Copy Source Files
Copy all Java files from the `cricket-score-example/src/` directory to your `app/src/main/java/com/example/cricketglyph/` directory.

#### Copy Resources
Merge the content from `cricket-score-example/res/values/strings.xml` into your `app/src/main/res/values/strings.xml`.

### 3. Configure build.gradle

Add to your `app/build.gradle`:

```gradle
android {
    namespace 'com.example.cricketglyph'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.cricketglyph"
        minSdk 29
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
}

dependencies {
    // Glyph Matrix SDK
    implementation files('libs/glyph-matrix-sdk-1.0.aar')

    // AndroidX
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
}
```

### 4. Configure AndroidManifest.xml

Add the following to your `AndroidManifest.xml`:

#### Permissions (inside `<manifest>`, before `<application>`)
```xml
<uses-permission android:name="com.nothing.ketchum.permission.ENABLE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

#### Service and Activity (inside `<application>`)
```xml
<!-- Cricket Score Glyph Toy Service -->
<service
    android:name=".CricketScoreToyService"
    android:exported="true">
    <intent-filter>
        <action android:name="com.nothing.glyph.TOY"/>
    </intent-filter>

    <meta-data
        android:name="com.nothing.glyph.toy.name"
        android:resource="@string/toy_name"/>
    <meta-data
        android:name="com.nothing.glyph.toy.image"
        android:resource="@drawable/cricket_score_preview"/>
    <meta-data
        android:name="com.nothing.glyph.toy.summary"
        android:resource="@string/toy_summary"/>
    <meta-data
        android:name="com.nothing.glyph.toy.introduction"
        android:value=".CricketScoreConfigActivity"/>
    <meta-data
        android:name="com.nothing.glyph.toy.longpress"
        android:value="1"/>
    <meta-data
        android:name="com.nothing.glyph.toy.aod_support"
        android:value="1"/>
</service>

<!-- Configuration Activity -->
<activity
    android:name=".CricketScoreConfigActivity"
    android:label="@string/config_title"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>
</activity>
```

### 5. Create Preview Image

#### Option A: Use Figma Template
1. Visit the [Figma template](https://www.figma.com/design/ryjvvPM2ZxI3OGdajSzb5J/Glyph-Toy--preview-icon-template)
2. Design your cricket ball icon (recommended: 1:1 ratio, 512x512px)
3. Use the [Figma plugin](https://www.figma.com/community/plugin/1526505846480298025) to convert
4. Export as SVG

#### Option B: Use Vector Asset Studio
1. In Android Studio: Right-click `res/drawable` → New → Vector Asset
2. Choose a cricket or sports icon from Material Icons
3. Name it: `cricket_score_preview`
4. Customize colors to match Glyph aesthetic (white on transparent)

#### Simple SVG Example
Create `app/src/main/res/drawable/cricket_score_preview.xml`:
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="512dp"
    android:height="512dp"
    android:viewportWidth="512"
    android:viewportHeight="512">

    <!-- Cricket ball -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M256,256m-200,0a200,200 0,1 1,400 0a200,200 0,1 1,-400 0"/>

    <!-- Seam lines -->
    <path
        android:strokeColor="#000000"
        android:strokeWidth="8"
        android:pathData="M156,200 Q256,180 356,200"/>
    <path
        android:strokeColor="#000000"
        android:strokeWidth="8"
        android:pathData="M156,312 Q256,332 356,312"/>
</vector>
```

### 6. Create Layout (Optional but Recommended)

Create `app/src/main/res/layout/activity_cricket_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <!-- Header -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/config_title"
            android:textSize="24sp"
            android:textStyle="bold"
            android:paddingBottom="8dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/config_subtitle"
            android:textSize="14sp"
            android:paddingBottom="16dp"/>

        <!-- Favorite Teams Section -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/teams_section_title"
            android:textSize="18sp"
            android:textStyle="bold"
            android:paddingTop="8dp"
            android:paddingBottom="8dp"/>

        <LinearLayout
            android:id="@+id/teams_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"/>

        <!-- API Settings Section -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/api_section_title"
            android:textSize="18sp"
            android:textStyle="bold"
            android:paddingTop="16dp"
            android:paddingBottom="8dp"/>

        <EditText
            android:id="@+id/api_key_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/api_key_hint"
            android:inputType="text"/>

        <EditText
            android:id="@+id/custom_api_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/custom_api_hint"
            android:inputType="textUri"/>

        <!-- Display Settings Section -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/display_section_title"
            android:textSize="18sp"
            android:textStyle="bold"
            android:paddingTop="16dp"
            android:paddingBottom="8dp"/>

        <TextView
            android:id="@+id/brightness_value"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Brightness: 100%"/>

        <SeekBar
            android:id="@+id/brightness_seeker"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="255"/>

        <TextView
            android:id="@+id/update_interval_value"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Update every: 30 seconds"
            android:paddingTop="8dp"/>

        <SeekBar
            android:id="@+id/update_interval_seeker"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="120"/>

        <CheckBox
            android:id="@+id/animations_checkbox"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/animations_label"
            android:checked="true"/>

        <!-- Buttons -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:paddingTop="16dp">

            <Button
                android:id="@+id/test_button"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/test_button"
                android:layout_marginEnd="8dp"/>

            <Button
                android:id="@+id/save_button"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/save_button"/>
        </LinearLayout>

        <Button
            android:id="@+id/activate_toy_button"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/activate_toy_button"
            android:textStyle="bold"
            android:backgroundTint="#FF0000"/>

    </LinearLayout>
</ScrollView>
```

### 7. Build and Install

#### Using Android Studio
1. Connect your Nothing Phone 3 via USB
2. Enable Developer Mode and USB Debugging
3. Click Run (green play button)
4. Select your device

#### Using Command Line
```bash
# Build the APK
./gradlew assembleDebug

# Install on device
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 8. First Run Configuration

1. Open the Cricket Score Glyph app on your phone
2. Select your favorite teams (or leave empty for all matches)
3. (Optional) Enter API key if you have one
4. Adjust display preferences
5. Tap "Test Connection" to verify
6. Tap "Activate Toy" to open Glyph Toys Manager
7. Find "Cricket Live Score" in the list and add it

### 9. Using the Toy

1. Press the Glyph Button on the back of your phone
2. Short presses cycle through available toys
3. When Cricket Live Score is displayed:
   - It will show live cricket scores
   - Long press to cycle through display modes
   - AOD support keeps it updated when screen is off

## Troubleshooting

### Build Errors

**Error: Cannot resolve symbol 'Common'**
- Solution: Use `Glyph.DEVICE_23112` instead of `Common.DEVICE_23112`

**Error: Package does not exist**
- Solution: Verify all import statements match your package name

**Error: Resource not found**
- Solution: Check that all string resources are defined in strings.xml

### Runtime Errors

**Service not appearing in Glyph Toys Manager**
- Check AndroidManifest.xml service declaration
- Verify all required meta-data tags are present
- Ensure preview image exists

**No scores displaying**
- Test internet connection
- Check API in configuration
- Try with mock data (automatically used as fallback)

**Glyph Matrix not lighting up**
- Verify device supports Glyph Matrix
- Check other Glyph Toys work
- Ensure system version is up to date

## Testing Without Device

While you can't fully test the Glyph Matrix without a Nothing Phone, you can:

1. **Test API Client**
   ```java
   CricketApiClient client = new CricketApiClient(context);
   client.getLiveMatches(new CricketApiClient.ApiCallback() {
       @Override
       public void onSuccess(List<CricketMatch> matches) {
           Log.d("TEST", "Found " + matches.size() + " matches");
       }

       @Override
       public void onError(String error) {
           Log.e("TEST", "Error: " + error);
       }
   });
   ```

2. **Test Configuration**
   ```java
   CricketScoreConfig config = new CricketScoreConfig(context);
   config.addFavoriteTeam("India");
   List<String> teams = config.getFavoriteTeams();
   Log.d("TEST", "Favorite teams: " + teams);
   ```

3. **Test Data Parsing**
   ```java
   // Use mock API response
   String mockResponse = "..."; // Your JSON here
   // Test parsing logic
   ```

## Next Steps

After successful installation:
1. Customize the display modes
2. Add your preferred API
3. Create custom animations
4. Share with the community!

## Support

- **Questions**: [Nothing Community](https://nothing.community/t/glyph-sdk)
- **SDK Issues**: [GDKsupport@nothing.tech](mailto:GDKsupport@nothing.tech)
- **API Issues**: Contact your API provider

---

Happy coding! 🏏✨
