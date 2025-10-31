# Cricket Live Score - Glyph Matrix Toy 🏏

![Version](https://img.shields.io/badge/version-1.0-blue)
![Platform](https://img.shields.io/badge/platform-Nothing%20Phone-red)
![License](https://img.shields.io/badge/license-MIT-green)

An AI-enhanced Glyph Matrix toy that displays live cricket scores on your Nothing Phone's LED matrix. Track your favorite teams and never miss a crucial moment!

## ✨ Features

### Core Functionality
- 🔴 **Real-time Score Updates** - Live scores updated every 30 seconds
- 🏏 **Animated Cricket Ball** - Smooth bouncing animation
- 📊 **Multiple Display Modes** - Score, Run Rate, Overs, Match Status
- ⭐ **Favorite Teams** - Filter matches by your favorite teams
- 🔄 **Smart Scrolling** - Auto-scrolling text for long team names
- 🌙 **AOD Support** - Always-On Display for continuous updates

### AI-Enhanced Features
- **Smart Team Detection** - Automatically shows matches when your favorite teams are playing
- **Intelligent Caching** - Reduces API calls while keeping data fresh
- **Auto-fallback** - Multiple API sources with automatic failover
- **Adaptive Display** - Optimizes content based on match state

### User Experience
- Long press Glyph Button to cycle through display modes
- Configurable update intervals (10-120 seconds)
- Adjustable brightness (0-255)
- Enable/disable animations
- Custom API endpoint support

## 🚀 Quick Start

### Prerequisites
- Nothing Phone 3 (or compatible device with Glyph Matrix)
- Android Studio Arctic Fox or later
- System version 20250829 or later (for some features)
- Java 8 or later

### Installation

1. **Clone or download this example**
   ```bash
   git clone <repository-url>
   cd cricket-score-example
   ```

2. **Copy the Glyph Matrix SDK**
   ```bash
   mkdir -p app/libs
   cp ../glyph-matrix-sdk-1.0.aar app/libs/
   ```

3. **Open in Android Studio**
   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

4. **Add to your build.gradle**
   ```gradle
   dependencies {
       implementation files('libs/glyph-matrix-sdk-1.0.aar')
   }
   ```

5. **Copy files to your project**
   - Copy all `.java` files from `src/` to your app's package
   - Copy `res/values/strings.xml` content to your strings.xml
   - Add the service and activity declarations from `AndroidManifest_snippet.xml` to your AndroidManifest.xml

6. **Build and install**
   ```bash
   ./gradlew installDebug
   ```

## 📱 Usage

### First Time Setup

1. **Install the app** on your Nothing Phone
2. **Open the app** to configure settings
3. **Select favorite teams** (optional - leave empty to show all matches)
4. **Configure API settings** (optional - uses free API by default)
5. **Tap "Test Connection"** to verify API access
6. **Tap "Activate Toy"** to open Glyph Toys Manager
7. **Add the toy** to your active toys list

### Using the Toy

1. **Press Glyph Button** to cycle through toys until Cricket Score is selected
2. **Long press Glyph Button** to change display mode:
   - **Score Mode**: Shows team scores with animated ball
   - **Run Rate Mode**: Shows current and required run rates
   - **Overs Mode**: Shows overs for each team
   - **Status Mode**: Shows match status message

### Display Modes

#### Score Display
```
🏏 🔴 LIVE: IND 185/5 (20.0) vs AUS 142/3 (15.2)
```

#### Run Rate Display
```
CRR: 9.25 | RRR: 9.50
```

#### Overs Display
```
IND: 20.0 | AUS: 15.2
```

#### Status Display
```
India need 44 runs from 28 balls
```

## 🔧 Configuration

### Favorite Teams
Select from popular international teams:
- India, Australia, England, Pakistan
- South Africa, New Zealand, Sri Lanka
- West Indies, Bangladesh, Afghanistan
- And more...

### API Settings

#### Using the Default Free API (CricketData.org)
No configuration needed! The app uses a free API by default with mock data fallback.

#### Using Custom API
1. Get your API key from [CricketData.org](https://cricketdata.org)
2. Enter API key in settings (optional for free tier)
3. Or use a custom API endpoint

#### Custom API Format
Your custom API should return JSON in this format:
```json
{
  "data": [
    {
      "id": "match_123",
      "name": "India vs Australia - T20",
      "matchType": "T20",
      "status": "live",
      "venue": "Mumbai",
      "teams": ["India", "Australia"],
      "score": {
        "team": [
          {"runs": "185", "wickets": "5", "overs": "20.0"},
          {"runs": "142", "wickets": "3", "overs": "15.2"}
        ]
      }
    }
  ]
}
```

### Display Preferences

| Setting | Range | Default | Description |
|---------|-------|---------|-------------|
| Brightness | 0-255 | 255 | LED brightness level |
| Update Interval | 10-120s | 30s | How often to fetch new scores |
| Animations | On/Off | On | Enable cricket ball animation |
| Scroll Speed | 50-200ms | 100ms | Text scrolling speed |

## 🏗️ Architecture

### Project Structure
```
cricket-score-example/
├── src/
│   ├── CricketScoreToyService.java      # Main service
│   ├── CricketApiClient.java            # API client
│   ├── CricketMatch.java                # Data model
│   ├── CricketScoreConfig.java          # Configuration
│   └── CricketScoreConfigActivity.java  # Settings UI
├── res/
│   └── values/
│       └── strings.xml                  # String resources
├── AndroidManifest_snippet.xml          # Manifest additions
├── build.gradle                         # Dependencies
└── README.md                            # This file
```

### Key Components

#### CricketScoreToyService
- Extends Android `Service`
- Implements Glyph Matrix lifecycle (onBind, onUnbind)
- Manages timers for updates and animations
- Handles Glyph Button events
- Renders frames to LED matrix

#### CricketApiClient
- Fetches data from cricket APIs
- Implements caching mechanism
- Auto-retry with exponential backoff
- Fallback to mock data for testing
- Thread-safe execution

#### CricketMatch
- Data model for match information
- Team abbreviation generation
- Helper methods for display

#### CricketScoreConfig
- SharedPreferences wrapper
- Manages user settings
- Provides default values
- Popular teams list

## 🎨 Customization

### Changing Animation
Edit `createCricketBallAnimation()` in `CricketScoreToyService.java`:
```java
private GlyphMatrixObject createCricketBallAnimation() {
    // Customize animation here
    int yOffset = (int) (Math.sin(mAnimationFrame * 0.3) * 3);
    // ...
}
```

### Adding Display Modes
1. Add mode to `DisplayMode` enum
2. Create display method (e.g., `displayCustomMode()`)
3. Add case in `displayCurrentMatch()`

### Using Different APIs
1. Implement parsing method in `CricketApiClient`
2. Add API URL constant
3. Update `fetchFromPrimaryApi()` or `fetchFromFallbackApi()`

### Customizing Text Display
Modify `formatScoreText()` in `CricketScoreToyService.java`:
```java
private String formatScoreText(CricketMatch match) {
    // Customize text format here
    StringBuilder sb = new StringBuilder();
    // ...
    return sb.toString();
}
```

## 🐛 Troubleshooting

### No matches showing
- Check internet connection
- Verify API key (if using paid tier)
- Test connection in settings
- Check if favorite teams are spelled correctly
- Try with empty favorite teams list

### Matrix not lighting up
- Ensure device has Glyph Matrix support
- Check system version (20250829+ for some features)
- Verify service is registered in AndroidManifest.xml
- Test with other Glyph Toys to confirm hardware works

### Slow updates
- Increase update interval
- Check API rate limits
- Verify network speed
- Enable caching

### Animation stuttering
- Reduce scroll speed
- Lower update frequency
- Disable animations
- Reduce brightness

## 📊 API Information

### Supported APIs

#### 1. CricketData.org (Default)
- **Free tier**: 100 requests/day
- **Rate limit**: Reasonable for personal use
- **Coverage**: ICC, IPL, BBL, and more
- **Documentation**: https://cricketdata.org/docs

#### 2. Entity Sport
- **Free tier**: Limited
- **Rate limit**: Check documentation
- **Coverage**: Extensive
- **Documentation**: https://www.entitysport.com/

#### 3. Custom API
- Configure in settings
- Must follow JSON format (see Configuration section)

### API Best Practices
- Use appropriate update intervals
- Implement caching
- Handle rate limits gracefully
- Provide fallback data

## 🔒 Permissions

Required permissions in AndroidManifest.xml:
```xml
<uses-permission android:name="com.nothing.ketchum.permission.ENABLE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

## 🚦 Performance Tips

1. **Optimize Update Frequency**
   - Use 30s for live matches
   - Use 60s+ for general monitoring
   - Enable caching

2. **Reduce Resource Usage**
   - Disable animations if not needed
   - Lower brightness
   - Limit number of favorite teams

3. **Network Efficiency**
   - Use WiFi when possible
   - Monitor API quota
   - Implement proper caching

## 🤝 Contributing

Contributions are welcome! Areas for improvement:
- Additional API integrations
- More animation styles
- Better error handling
- UI enhancements
- Performance optimizations

## 📄 License

This example is provided under the MIT License. See LICENSE file for details.

## 🙏 Acknowledgments

- Nothing for the Glyph Matrix Developer Kit
- Cricket API providers (CricketData.org, Entity Sport)
- Android developer community
- AI enhancement for intelligent features

## 📞 Support

For issues and questions:
- **GDK Issues**: [GDKsupport@nothing.tech](mailto:GDKsupport@nothing.tech)
- **Community**: [Nothing Community](https://nothing.community/t/glyph-sdk)
- **API Issues**: Check respective API provider documentation

## 🔮 Future Enhancements

Planned features:
- [ ] Player statistics display
- [ ] Match notifications
- [ ] Score predictions
- [ ] Historical match data
- [ ] Custom team logos
- [ ] Voice score updates
- [ ] Widget support
- [ ] Multi-language support

---

**Built with ❤️ using AI enhancement for Nothing Phone Glyph Matrix**

*Version 1.0 - Last updated: 2025*
