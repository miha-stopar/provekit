#!/bin/bash

# Setup test files for the Android app

echo "Setting up test files for Android ProveKit demo..."

# Create assets directory
mkdir -p android-example/app/src/main/assets

# For now, let's copy one of the example schemes from noir-examples
# We'll use the basic example as it's simple

BASIC_EXAMPLE="noir-examples/basic"

if [ -d "$BASIC_EXAMPLE" ]; then
    echo "Copying basic example files..."
    
    # Copy the Noir source (for reference)
    cp -r "$BASIC_EXAMPLE/src" "android-example/app/src/main/assets/noir_src"
    
    # Copy prover config if it exists
    if [ -f "$BASIC_EXAMPLE/Prover.toml" ]; then
        cp "$BASIC_EXAMPLE/Prover.toml" "android-example/app/src/main/assets/"
    fi
    
    echo "Note: You'll need to compile the Noir program first to get the .pkp file"
    echo "Run: cd $BASIC_EXAMPLE && nargo compile"
    
else
    echo "Basic example not found. Creating dummy files..."
    
    # Create dummy input file
    cat > android-example/app/src/main/assets/input.toml << 'EOF'
# Example input for ProveKit demo
# This is a dummy file - replace with actual witness values

[main]
x = 10
y = 20
expected_sum = 30
EOF

    echo "Created dummy input.toml"
fi

echo "Test file setup complete!"
echo ""
echo "Next steps:"
echo "1. Build the Android app"
echo "2. Install on device/emulator"
echo "3. The app will try to generate a proof with the test files"