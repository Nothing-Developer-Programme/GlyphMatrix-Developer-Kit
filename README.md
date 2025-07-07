# Glyph Matrix Developer Kit 


The Glyph Matrix Developer Kit provides everything you need to know before creating a custom Glyph Matrix experience **in your app** or **building your own Glyph Toy**  on compatible devices.

At its core is the GlyphMatrixSDK—an Android library that can convert your designs into Glyph Matrix Data and rendering it frame by frame on the Glyph Matrix. It also provides you with identifiers so you can handle events relevant to Glyph Button.


This documentation contains the following three sections

- [**Getting Started**](#getting-started): How to integrate the GlyphMatrixSDK and configure your development environment
- [**Developing a Glyph Toy Service**](#developing-a-glyph-toy-service): How to manage the service life cycle for Glyph Toy, handle interaction, and behavior of toy service if it has AOD capability.
- [**API Reference**](#api-reference): Complete documentation of classes, methods

Note that while the sample code in this README is written in Java, we also provide a complete demo project for learning building Glyph Toys that you can reference in [GlyphMatrix-Example-Project](https://github.com/KenFeng04/GlyphMatrix-Example-Project)

## Getting Started


### 1. GlyphMatrixSDK Integration

1. After creating a new Android project, create a libs folder under your main app module.
2. Copy the SDK library e.g. GlyphMatrixSDK.aar file from this repository into the libs directory.
3. Add the SDK as a library dependency in your build.gradle file. If you're using Android Studio, you can also refer to the "Add your AAR or JAR as a dependency" section on [developer.android.com](https://developer.android.com/studio/projects/android-library#psd-add-aar-jar-dependency) to learn
 how to do it, make sure the path you use is something like "libs/GlyphMatrixSDK.aar"

### 2. AndroidManifest.xml Configuration

You can find the AndroidManifest.xml file at the following path  `<your-project>/app/src/main/AndroidManifest.xml`

#### 2.1. Required Permission

Add the following line within the `<manifest>` tag in AndroidManifest.xml

```xml
<uses-permission android:name="com.nothing.ketchum.permission.ENABLE"/>
```

#### 2.2 Service Registration For Glyph Toys

> **Note**: This section is only required if you're developing Glyph Toys. If you're only integrating the GlyphMatrixSDK into your existing application, you can skip this section.

To ensure your Nothing Phone setting recognises and can display your Glyph Toys, you'll need to register each toy as a service within the `<application>` tag of your  `AndroidManifest.xml ` file.

The following code demonstrates how to register two toys. Each service includes metadata for the toy's name, preview thumbnail image (displayed on the Glyph Toys settings page), and supported behaviour. The first example provides a complete setup with optional features, while the second shows a minimal configuration.

To create a Glyph Toy preview/ thumbnail you can reference this [thumbnail template](https://www.figma.com/design/ryjvvPM2ZxI3OGdajSzb5J/Glyph-Toy--Thumbnail-template?node-id=1-12&t=9fImWPH8fzC7GO9x-1) in Figma

**Note**: Replace the class names, and resource references in the examples below with your own before using.<br>

```xml
<!-- Full Example: Registers a Glyph toy with complete metadata for enhanced functionality -->
<!-- Replace "com.nothing.demo.TestToyOne" with your service class -->
<service android:name="com.nothing.demo.TestToyOne"
    android:exported="true">
    <intent-filter>
        <action android:name="com.nothing.glyph.TOY"/>
    </intent-filter>

    <!-- Required: Ensures the toy appears in the Glyph Toys manager list -->
    <meta-data
        android:name="com.nothing.glyph.toy.name"
        android:resource="@string/toy_name_one"/>  <!-- Replace with your string resource -->

    <!-- Required: Allows users to preview your toy in the settings -->
    <meta-data
        android:name="com.nothing.glyph.toy.image"
        android:resource="@drawable/img_toy_preview_one"/>  <!-- Replace with your image resource -->

    <!-- Optional: Provides a brief description of your toy -->
    <meta-data
        android:name="com.nothing.glyph.toy.summary"
        android:resource="@string/toy_summary" />  <!-- Replace with your string resource -->

    <!-- Optional: Links to a detailed introduction page for your toy -->
    <meta-data
        android:name="com.nothing.glyph.toy.introduction"
        android:value="com.yourPackage.yourToyIntroduceActivity" />  <!-- Replace with your activity class -->

    <!-- Optional: Enables long press functionality, default is 0 -->
    <meta-data
        android:name="com.nothing.glyph.toy.longpress"
        android:value="1"/>

    <!-- Optional: Indicates support for Always-On Display (AOD) -->
    <meta-data
        android:name="com.nothing.glyph.toy.aod_support"
        android:value="1"/>
</service>

<!-- Minimum Example: Registers a basic Glyph toy with essential metadata -->
<service android:name="com.nothing.demo.TestToySecond"
    android:exported="true">
    <intent-filter>
        <action android:name="com.nothing.glyph.TOY"/>
    </intent-filter>

    <!-- Required: Ensures the toy appears in the Glyph Toys manager list -->
    <meta-data
        android:name="com.nothing.glyph.toy.name"
        android:resource="@string/toy_name_second"/>

    <!-- Required: Allows users to preview your toy in the settings -->
    <meta-data
        android:name="com.nothing.glyph.toy.image"
        android:resource="@drawable/img_toy_preview_second"/>
</service>
```

## Developing a Glyph Toy Service

### User Interaction

`Glyph Toys` are commonly controlled using `Glyph Button` on the back of the device. There are mainly three types of interaction with `Glyph Button`:

- **Short press**: A quick press on `Glyph Button` cycles through available toys. When your toy is selected and shown on the `Glyph Matrix`, its functions start. Tapping again navigates to the next toy. 
- **Long press**: A long press on `Glyph Button` sends a `"change"` event to the currently selected toy, triggering an action you defined. For example, in the preinstalled camera toy, the first long press activates the camera and subsequent presses capture photos. For the timer toy, it toggles start/stop. (This feature must be enabled in `AndroidManifest.xml`).
- **Touch-down & Touch-up**: If the user keeps the `Glyph Button` held down & released, it will trigger `"action_down"` & `"action_up"` events 

Additionally, toys can also utilize other control inputs such as the device's gyroscope and accelerometer, to create more engaging experiences like the `Magic 8 Ball` & `Leveler Toy` we co-created with community members. Please note that only one toy can be activated at a time.

### Development Implementation

#### Responding to Toy Selection (Managing the Lifecycle)

When a user selects your toy, the system binds your Service. You must implement onBind() to start its functions and onUnbind() to stop them cleanly.

The following example shows how to manage the start/stop lifecycle.

```java
@Override
public IBinder onBind(Intent intent) {
    init();// Experience that you want to create when Glyph Toy is selected
    return null;
}

@Override
public boolean onUnbind(Intent intent) {
    mGM.unInit();
    mGM = null;
    mCallback = null;
    return false;
}
```

#### Handling the "Change" Event

Glyph Button relevant event is handled using GlyphToy class. To react with user's interaction to the Glyph Button, you must create a `Handler` to process the event and a `Messenger` to communicate with the system. The `IBinder` from this `Messenger` must be returned by your `onBind()` method.

The code below shows how to receive and handle the event, in this case the long press (change) event.

```java
@Override
public IBinder onBind(Intent intent) {
    init();
    return serviceMessenger.getBinder();
}

private final Handler serviceHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case GlyphToy.MSG_GLYPH_TOY: {
                Bundle bundle = msg.getData();
                String event = bundle.getString(GlyphToy.MSG_GLYPH_TOY_DATA);
                if (GlyphToy.EVENT_CHANGE.equals(event)) {
                    // Your reaction for the long press.
                }
                break;
            }
            default:
                super.handleMessage(msg);
        }
    }
};
private final Messenger serviceMessenger = new Messenger(serviceHandler);
```

### Toy with AOD capability
If you have set your toy as an AOD toy, your toy will receive EVENT_AOD every minutes when your toy has been selected as aod toy.



```java
private final Handler serviceHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case GlyphToy.MSG_GLYPH_TOY: {
                Bundle bundle = msg.getData();
                String event = bundle.getString(GlyphToy.MSG_GLYPH_TOY_DATA);
                if (GlyphToy.EVENT_AOD.equals(event)) {
                    // Your action for aod
                }
                break;
            }
            default:
                super.handleMessage(msg);
        }
    }
};
```



## API Reference

### GlyphMatrixManager

GlyphMatrixManager is responsible for:

- **Connecting to and disconnecting from** an underlying service or device.
- **Registering your application** to use that service/device.
- **Updating the display** of a Glyph Matrix with either raw color data or structured frame objects.

#### Public Methods

| Return type                    | Method                         | Description                    |
|:-------------------------------|:-------------------------------|:-------------------------------|
| void           | `init(Callback callback)`       | Used for binding the Service. It is recommended to be created when components start.            |
| void           | `unInit()`                      | Used for unbinding the Service. It is recommended to be destroyed when components end.          |
| void           | `register(String target)`       | Register your app for service. You need to send which target device you are working on. For Phone 3, this should be Glyph.DEVICE_23112|
| void           | `setMatrixFrame(int[] color)`   | Updates the Glyph Matrix display using raw color data. This overload expects a 25x25 integer array. |
| void           | `setMatrixFrame(GlyphMatrixFrame)` | Updates the Glyph Matrix display using a structured GlyphMatrixFrame object.                 |

### GlyphMatrixFrame

GlyphMatrixFrame is in charge of handling and displaying the Glyph Matrix. Its default size is 25x25, but you can easily customise it using the Builder. 

<p align="center">
  <img src="image/pixel_coordinate.png" alt="Example of recommended icon locations">
</p>

Developers can use this class to render GlyphMatrix objects into the necessary Matrix data and control how multiple objects can be overlaid on the Glyph Matrix based on the layer they are in using the builder.

#### Public Methods

| Return type                    | Method name                    | Description                    |
|:-------------------------------|:-------------------------------|:-------------------------------|
| int[]                          |  `render()`              | Renders all objects previously added via the **Builder** and integrates them into the corresponding **Glyph Matrix data**. The implementation of this method may vary depending on project requirements. This method can either directly drive hardware to display on an actual Glyph Matrix, or return an array or bitmap data to the caller for further processing. |

### GlyphMatrixFrame.Builder

GlyphMatrixFrame.Builder is a static inner class that uses the Builder pattern to help you construct and configure a GlyphMatrixFrame. This frame is essentially an array of data that defines how each LED on the matrix should light up, and it can be read by the GlyphMatrixManager.

You can have maximum 3 GlyphMatrixObject for each GlyphMatrixFrame,1 for each layer before you finally call `build()` to generate your finished GlyphMatrixFrame instance.

#### Public Constructors

| Return type                    | Method name                    | Description                    |
|:-------------------------------|:-------------------------------|:-------------------------------|
| N/A            | `Builder()`                     | Initializes a new `Builder` instance. This creates an empty `GlyphMatrixFrame.Builder` ready for configuration. By default, the Matrix size is set to 25x25. |

#### Public Methods

| Return type                    | Method                         | Description                    |
|:-------------------------------|:-------------------------------|:-------------------------------|
| void           | `addTop(GlyphMatrixObject object)` | Adds object to top layer, rendered above middle and low layers |
| void           | `addMid(GlyphMatrixObject object)` | Adds object to middle layer, rendered between top and low layers |
| void           | `addLow(GlyphMatrixObject object)` | Adds object to bottom layer, rendered below top and middle layers |
| GlyphMatrixFrame | `build(Context context)`        | Constructs and returns a new GlyphMatrixFrame instance based on the currently accumulated settings. This instance is ready for display or further manipulation. |
#### Example
The following example shows how to create a GlyphMatrixFrame with a butterfly object on the top layer, and then render it to the actual Glyph Matrix using mGM (GlyphMatrixManager), the class mentioned above that manages the actual matrix.
```java
GlyphMatrixFrame.Builder frameBuilder = new GlyphMatrixFrame.Builder();
GlyphMatrixFrame frame = frameBuilder.addTop(butterfly).build();
mGM.setMatrixFrame(frame.render());
```

### GlyphMatrixObject

The GlyphMatrixObject encapsulates a single image or frame for display on the Glyph Matrix, along with its configurable properties.

These properties, such as image source, position coordinates (X, Y), clockwise rotation angle, scaling, brightness, and transparency, can all be adjusted using the `GlyphMatrixObject.Builder`.

#### Public Accessor Methods

| Return type                    | Method name                    | Description                    |
|:-------------------------------|:-------------------------------|:-------------------------------|
| Object         | `getImageSource()`            | Returns the image source object|
| Int            | `getPositionX()`              | Returns the X-coordinate of the object's top-left corner.                                       |
| Int            | `getPositionY()`              | Returns the Y-coordinate of the object's top-left corner.                                       |
| Int            | `getOrientation()`            | Returns the counterclockwise rotation angle of the object from the original (default: 0)       |
| Int            | `getScale()`                  | Returns the **scaling factor** of the object. (0-200, default: 100)                           |
| Int            | `getBrightness()`             | Returns the brightness **level** of the object. (0-255, default: 255)                         |

### GlyphMatrixObject.Builder

GlyphMatrixObject.Builder is a static inner class of GlyphMatrixObject, used to create and configure an image object's display parameters.

#### Public Constructors

| Return type                    | Method name                    | Description                    |
|:-------------------------------|:-------------------------------|:-------------------------------|
| N/A            | `Builder()`                     | If not explicitly set by other methods, the `Builder` will use the following default values for the `GlyphMatrixObject`:<br/>- Position: (0, 0)<br/>- Orientation: 0 degrees<br/>- Scale: 100<br/>- Brightness: 255 |

#### Public Methods

| Return type                    | Method name                    | Description                    |
|:-------------------------------|:-------------------------------|:-------------------------------|
| void           | `setImageSource(Object imagesource)` | Sets the image source. Must be a 1:1 bitmap - other formats require conversion. Higher resolutions may impact performance. |
| void           | `setText(String text)`          | Sets the string content to be displayed in the Glyph Matrix.                                   |
| void           | `setPosition(int x, int y)`     | Sets the object's top-left corner position on the Glyph Matrix                                 |
| void           | `setOrientation(int)`           | Sets the object's clockwise rotation angle in degrees, anchored at center. 0 = no rotation (default). Values normalized to 0-360° range |
| void           | `setBrightness(int brightness)` | Sets the brightness level of the object.<br/><br/>Valid range is from 0: LED off - 255 :brightest (default) |
| void           | `setScale(int scale)`           | Sets the scaling factor for the object. The anchor point is in the middle of the object<br/><br/>Valid range: 0-200 0: Object is not visible 100: Original size (default) 200: Double size |
| GlyphMatrixObject | `build()`                    | Constructs and returns a GlyphMatrix Object instance based on the current settings.             |

### Example
The following example shows how to construct a GlyphMatrixObject called butterfly.

```java
GlyphMatrixObject.Builder butterflyBuilder = new GlyphMatrixObject.Builder();
GlyphMatrixObject butterfly = butterflyBuilder
.setImageSource(GlyphMatrixUtils.drawableToBitmap(getResources().getDrawable(R.drawable.butterfly)))
.setScale(100)
.setOrientation(0)
.setPosition(0, 0)
.setReverse(false)
.build();
```

## Support

If you've found an error in this kit, please file an issue.

If there is any problem related to development,you can contact: [GDKsupport@nothing.tech](mailto:GDKsupport@nothing.tech)

However, you may get a faster response from our [community](https://nothing.community/t/glyph-sdk)

## See Also

For a practical demo project on building Glyph Toys, see the [GlyphMatrix-Example-Project](https://github.com/KenFeng04/GlyphMatrix-Example-Project)<br>
Kits for building a Glyph Interface experience around devices with a Glyph Light Stripe [Glyph-Developer-Kit](https://github.com/Nothing-Developer-Programme/Glyph-Developer-Kit)
