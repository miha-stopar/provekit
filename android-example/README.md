# ProveKit Android Demo

This is a simple Android application that demonstrates how to use ProveKit FFI (Foreign Function Interface) to generate zero-knowledge proofs on Android devices.

## What This Demo Does

1. **Initializes ProveKit** native library on app startup
2. **Provides circuit selection** via dropdown with multiple available circuits
3. **Demonstrates actual proof generation** for various circuits:
   - **Basic Poseidon**: Simple Poseidon2 hash of two field elements
   - **Poseidon Rounds**: Hash with 1000 additional rounds for increased complexity
   - Easy to add more circuits by following the structure
4. **Shows detailed circuit information** including description, complexity, and estimated proving time
5. **Supports two proof methods**:
   - Generate proof and save to file
   - Generate proof and return as JSON string
6. **Shows error handling** and logging for debugging

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
│   │   │   ├── MainActivity.java          # Main app logic with circuit selection
│   │   │   ├── Circuit.java              # Circuit representation class
│   │   │   ├── CircuitManager.java       # Handles loading and managing circuits
│   │   │   └── ProveKitFFI.java          # Java FFI interface
│   │   ├── cpp/
│   │   │   ├── CMakeLists.txt            # Native build config
│   │   │   └── provekit_jni.cpp          # JNI wrapper (C++)
│   │   ├── jniLibs/
│   │   │   ├── arm64-v8a/
│   │   │   │   └── libprovekit_ffi.so    # ARM64 native library
│   │   │   └── x86_64/
│   │   │       └── libprovekit_ffi.so    # x86_64 native library
│   │   ├── assets/
│   │   │   └── circuits/                 # Circuit definitions and files
│   │   │       ├── basic/                # Simple Poseidon hash circuit
│   │   │       │   ├── circuit.json      # Circuit metadata
│   │   │       │   ├── prover.pkp        # Prover key
│   │   │       │   └── input.toml        # Witness input
│   │   │       └── poseidon-rounds/      # Complex Poseidon rounds circuit
│   │   │           ├── circuit.json      # Circuit metadata
│   │   │           ├── prover.pkp        # Prover key
│   │   │           └── input.toml        # Witness input
│   │   ├── res/                          # Android resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle                      # App build configuration
│   └── proguard-rules.pro
├── build.gradle                          # Project build configuration
├── settings.gradle                       # Project settings
├── gradle.properties                     # Gradle properties
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
3. **Select a circuit** from the dropdown - you'll see a description of what each circuit proves
4. **Tap "Generate Proof"** to generate a proof for the selected circuit
5. **View results** in the scrollable output area

## Adding New Circuits

To add a new circuit to the app:

1. **Create a directory** in `app/src/main/assets/circuits/` with your circuit name
2. **Add the following files**:
   - `prover.pkp` - Generated by `provekit-cli prepare`
   - `input.toml` - Witness input for your circuit
   - `circuit.json` - Metadata describing the circuit:
   ```json
   {
     "name": "Your Circuit Name",
     "description": "What this circuit proves",
     "proverFile": "prover.pkp",
     "inputFile": "input.toml",
     "inputs": {
       "input1": "Description of first input",
       "input2": "Description of second input"
     },
     "complexity": "Low/Medium/High",
     "estimatedProvingTime": "X-Y seconds"
   }
   ```
3. **Rebuild the app** - the circuit will automatically appear in the dropdown

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

1. **Circuit selection is build-time** - circuits must be included in the APK assets
2. **Only supports ARM64 and x86_64** - ARMv7 build failed due to architecture constraints
3. **Basic error handling** - production app would need more robust error management
4. **No UI polish** - this is a technical demonstration, not a production app

## Next Steps

To make this a real application:

1. **Add more Noir circuits** by following the circuit addition guide above
2. **Dynamic circuit loading** from external storage or network
3. **Add progress indicators** for long-running proof generation
4. **Improve error handling** and user feedback
5. **Add proof verification** functionality using verifier keys
6. **Optimize for production** (smaller APK size, better performance)
7. **Add circuit parameter customization** to modify witness inputs

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
- Check logcat for specific error messages
- Ensure the prover.pkp file was generated correctly using `provekit-cli prepare`
- Verify input format matches the circuit's expected witness format