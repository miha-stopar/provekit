#!/bin/bash

# Simple Android NDK setup script
# This downloads a standalone NDK for cross-compilation

set -e

NDK_VERSION="26.1.10909125"
NDK_DIR="$HOME/android-ndk-$NDK_VERSION"

echo "Setting up Android NDK for ProveKit FFI..."

# Check if NDK already exists
if [ -d "$NDK_DIR" ]; then
    echo "NDK already exists at $NDK_DIR"
else
    echo "Downloading Android NDK $NDK_VERSION..."
    
    # Determine platform
    if [[ "$OSTYPE" == "darwin"* ]]; then
        NDK_PLATFORM="darwin"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        NDK_PLATFORM="linux"
    else
        echo "Unsupported platform: $OSTYPE"
        exit 1
    fi
    
    NDK_ZIP="android-ndk-r26b-$NDK_PLATFORM.zip"
    NDK_URL="https://dl.google.com/android/repository/$NDK_ZIP"
    
    echo "Downloading from $NDK_URL"
    curl -L -o "/tmp/$NDK_ZIP" "$NDK_URL"
    
    echo "Extracting NDK..."
    cd "$HOME"
    unzip -q "/tmp/$NDK_ZIP"
    mv "android-ndk-r26b" "$NDK_DIR"
    
    echo "Cleaning up..."
    rm "/tmp/$NDK_ZIP"
fi

# Set environment variables
export ANDROID_NDK_HOME="$NDK_DIR"
export ANDROID_NDK_ROOT="$NDK_DIR"

echo ""
echo "NDK setup complete!"
echo "Add these to your shell profile (~/.zshrc or ~/.bashrc):"
echo "export ANDROID_NDK_HOME=\"$NDK_DIR\""
echo "export ANDROID_NDK_ROOT=\"$NDK_DIR\""
echo ""
echo "For this session:"
echo "export ANDROID_NDK_HOME=\"$NDK_DIR\""
echo "export ANDROID_NDK_ROOT=\"$NDK_DIR\""