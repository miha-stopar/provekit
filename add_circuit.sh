#!/bin/bash
# Script to add a new circuit to the Android demo app
# Usage: ./add_circuit.sh <circuit_name> <noir_example_dir>

set -e

CIRCUIT_NAME="$1"
NOIR_DIR="$2"
ASSETS_DIR="android-example/app/src/main/assets/circuits"

if [ -z "$CIRCUIT_NAME" ] || [ -z "$NOIR_DIR" ]; then
    echo "Usage: $0 <circuit_name> <noir_example_dir>"
    echo "Example: $0 sha256 noir-examples/sha256"
    exit 1
fi

if [ ! -d "$NOIR_DIR" ]; then
    echo "Error: Noir directory '$NOIR_DIR' does not exist"
    exit 1
fi

echo "Adding circuit '$CIRCUIT_NAME' from '$NOIR_DIR'..."

# Create circuit directory
mkdir -p "$ASSETS_DIR/$CIRCUIT_NAME"

# Compile the circuit
echo "Compiling circuit..."
cd "$NOIR_DIR"
nargo compile

# Prepare prover key
echo "Preparing prover key..."
cargo run --release --bin provekit-cli prepare ./target/basic.json --pkp ./prover.pkp --pkv ./verifier.pkv

# Copy files
echo "Copying circuit files..."
cp prover.pkp "../../$ASSETS_DIR/$CIRCUIT_NAME/"
cp Prover.toml "../../$ASSETS_DIR/$CIRCUIT_NAME/input.toml"

# Generate circuit metadata (basic template)
cd "../../"
cat > "$ASSETS_DIR/$CIRCUIT_NAME/circuit.json" << EOF
{
  "name": "$CIRCUIT_NAME",
  "description": "Generated circuit from $NOIR_DIR",
  "proverFile": "prover.pkp",
  "inputFile": "input.toml",
  "inputs": {
    "witness": "Check the input.toml file for specific parameters"
  },
  "complexity": "Unknown",
  "estimatedProvingTime": "Unknown - test to determine"
}
EOF

echo "Circuit '$CIRCUIT_NAME' added successfully!"
echo "Files created in: $ASSETS_DIR/$CIRCUIT_NAME/"
echo "Please review and customize the circuit.json metadata file."
echo "Then rebuild the Android app to see the new circuit in the dropdown."