# ProveKit Android Demo

This is a simple Android application that demonstrates how to use ProveKit FFI (Foreign Function Interface) to generate zero-knowledge proofs on Android devices.

## What This Demo Does

1. **Initializes ProveKit** native library on app startup
2. **Provides a simple UI** with a button to generate proofs
3. **Demonstrates two proof methods**:
   - Generate proof and save to file
   - Generate proof and return as JSON string
4. **Shows error handling** and logging for debugging

## Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────┐
│   Android App   │    │   JNI Wrapper    │    │   ProveKit Rust     │
│   (Java/Kotlin) │◄──►│   (C++)          │◄──►│   (Native Library)  │
└─────────────────┘    └──────────────────┘    └─────────────────────┘
        │                       │                        │
        │                       │                        │
        ▼                       ▼                        ▼
   UI & Logic            Native Bridge            Proof Generation
```

## Files Structure

```
android-example/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/provekitdemo/
│   │   │   ├── MainActivity.java          # Main app logic
│   │   │   └── ProveKitFFI.java          # Java FFI interface
│   │   ├── cpp/
│   │   │   ├── CMakeLists.txt            # Native build config
│   │   │   └── provekit_jni.cpp          # JNI wrapper (C++)
│   │   ├── jniLibs/
│   │   │   ├── arm64-v8a/
│   │   │   │   └── libprovekit_ffi.so    # ARM64 native library
│   │   │   └── x86_64/
│   │   │       └── libprovekit_ffi.so    # x86_64 native library
│   │   ├── res/                          # Android resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle                      # App build configuration
│   └── proguard-rules.pro
├── build.gradle                          # Project build configuration
├── settings.gradle                       # Project settings
├── gradle.properties                     # Gradle properties
└── build-android.sh                      # Build script
```

## Build Requirements

1. **Android SDK** (via Android Studio)
2. **Android NDK** (for native code compilation)
3. **ProveKit FFI libraries** (built for Android)
4. **Java 11+** for Gradle

## Building the App

### Step 1: Ensure ProveKit FFI is built for Android

The libraries should already be in place at:
- `app/src/main/jniLibs/arm64-v8a/libprovekit_ffi.so`
- `app/src/main/jniLibs/x86_64/libprovekit_ffi.so`

If missing, rebuild them:
```bash
# From the provekit root directory
ANDROID_NDK_HOME="$HOME/android-ndk-26.1.10909125" \\
cargo ndk -t arm64-v8a -t x86_64 build --release -p provekit-ffi

# Copy to Android project
cp target/aarch64-linux-android/release/libprovekit_ffi.so android-example/app/src/main/jniLibs/arm64-v8a/
cp target/x86_64-linux-android/release/libprovekit_ffi.so android-example/app/src/main/jniLibs/x86_64/
```

### Step 2: Build the Android APK

```bash
cd android-example
./build-android.sh
```

### Step 3: Install on Device/Emulator

```bash
# Install via ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Or open in Android Studio and run
```

## Usage

1. **Launch the app** on your Android device/emulator
2. **Wait for initialization** - you'll see "ProveKit initialized successfully!"
3. **Tap "Generate Proof"** to test proof generation
4. **View results** in the scrollable output area

## Debugging

- **Check logcat** for detailed logs:
  ```bash
  adb logcat | grep ProveKitFFI
  ```
- **Common issues**:
  - Native library not found: Check that `.so` files are in the right `jniLibs` folders
  - Initialization failed: Check that ProveKit FFI was built correctly for your target architecture
  - Proof generation errors: Currently using dummy input files - real proof would need proper Noir program and witness

## Current Limitations

1. **Demo uses dummy files** - replace with real Noir program and witness for actual proofs
2. **Only supports ARM64 and x86_64** - ARMv7 build failed due to architecture constraints
3. **Basic error handling** - production app would need more robust error management
4. **No UI polish** - this is a technical demonstration, not a production app

## Next Steps

To make this a real application:

1. **Add real Noir programs** and proper witness generation
2. **Implement file picker** to select prover and input files
3. **Add progress indicators** for long-running proof generation
4. **Improve error handling** and user feedback
5. **Add proof verification** functionality
6. **Optimize for production** (smaller APK size, better performance)

## Troubleshooting

### "Native library not found"
- Ensure `.so` files are in the correct `jniLibs` subdirectories
- Check that the target architecture matches your device/emulator

### "ProveKit initialization failed"
- Check logcat output for specific error messages
- Verify that ProveKit FFI was compiled with Android support

### Build failures
- Ensure Android SDK and NDK are properly installed
- Check that `ANDROID_HOME` environment variable is set
- Make sure Java 11+ is available

### Proof generation errors
- Currently expected - demo uses dummy input files
- Replace with real Noir program artifacts for actual proof generation