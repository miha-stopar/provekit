package com.example.provekitdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Android demo app for ProveKit FFI.
 * 
 * This app demonstrates generating zero-knowledge proofs on Android using
 * various circuits from the noir-examples collection.
 * 
 * Supports multiple circuits including:
 * - Basic Poseidon: Simple hash of two field elements
 * - Poseidon Rounds: Hash with 1000 additional rounds
 * - And more circuits can be easily added
 */
public class MainActivity extends AppCompatActivity {

    private TextView textViewStatus;
    private TextView textViewOutput;
    private TextView textViewCircuitDescription;
    private Button buttonGenerateProof;
    private Spinner spinnerCircuits;
    private CircuitManager circuitManager;
    private Circuit selectedCircuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize circuit manager
        circuitManager = new CircuitManager(this);

        // Initialize views
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewOutput = findViewById(R.id.textViewOutput);
        textViewCircuitDescription = findViewById(R.id.textViewCircuitDescription);
        buttonGenerateProof = findViewById(R.id.buttonGenerateProof);
        spinnerCircuits = findViewById(R.id.spinnerCircuits);

        // Set up circuit selector
        setupCircuitSelector();

        // Initialize ProveKit in background
        initializeProveKit();

        // Set button click listener
        buttonGenerateProof.setOnClickListener(v -> generateProof());
    }

    private void setupCircuitSelector() {
        List<Circuit> circuits = circuitManager.getAvailableCircuits();
        
        if (circuits.isEmpty()) {
            textViewStatus.setText("Error: No circuits found in assets");
            return;
        }

        // Create adapter for spinner
        ArrayAdapter<Circuit> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, circuits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCircuits.setAdapter(adapter);

        // Set up selection listener
        spinnerCircuits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedCircuit = circuits.get(position);
                updateCircuitDescription();
                updateGenerateButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCircuit = null;
                updateGenerateButton();
            }
        });

        // Select first circuit by default
        if (!circuits.isEmpty()) {
            selectedCircuit = circuits.get(0);
            updateCircuitDescription();
        }
    }

    private void updateCircuitDescription() {
        if (selectedCircuit != null) {
            textViewCircuitDescription.setText(selectedCircuit.getDetailedDescription());
        } else {
            textViewCircuitDescription.setText("");
        }
    }

    private void updateGenerateButton() {
        // Enable button only if ProveKit is initialized and a circuit is selected
        boolean proveKitReady = textViewStatus.getText().toString().contains("initialized successfully");
        buttonGenerateProof.setEnabled(proveKitReady && selectedCircuit != null);
    }

    private void initializeProveKit() {
        textViewStatus.setText("Initializing ProveKit...");
        
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    // Initialize ProveKit FFI
                    return ProveKitFFI.init();
                } catch (UnsatisfiedLinkError e) {
                    // Native library not found or not compatible
                    return -1;
                } catch (Exception e) {
                    return -2;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == ProveKitFFI.PK_SUCCESS) {
                    textViewStatus.setText("ProveKit initialized successfully!");
                    updateGenerateButton();
                } else if (result == -1) {
                    textViewStatus.setText("Error: Native library not found. Make sure libprovekit_ffi.so is included in APK.");
                } else if (result == -2) {
                    textViewStatus.setText("Error: Exception during initialization.");
                } else {
                    textViewStatus.setText("ProveKit initialization failed: " + ProveKitFFI.getErrorMessage(result));
                }
            }
        }.execute();
    }

    private void generateProof() {
        if (selectedCircuit == null) {
            Toast.makeText(this, "Please select a circuit first", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonGenerateProof.setEnabled(false);
        textViewOutput.setText("Generating proof for: " + selectedCircuit.getName() + "\n\n" +
                              selectedCircuit.getDescription() + "\n\n" +
                              "Estimated time: " + selectedCircuit.getEstimatedProvingTime() + "\n\n" +
                              "Please wait...");

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    // Use the selected circuit's files
                    
                    // Copy prover key and input from assets to internal storage
                    String proverPath = copyAssetToInternalStorage(selectedCircuit.getProverAssetPath(), 
                                                                  selectedCircuit.getProverFile());
                    String inputPath = copyAssetToInternalStorage(selectedCircuit.getInputAssetPath(), 
                                                                 selectedCircuit.getInputFile());
                    
                    if (proverPath == null || inputPath == null) {
                        return "Error: Could not copy circuit files from assets";
                    }

                    // Try to generate proof as JSON
                    String jsonResult = ProveKitFFI.proveToJson(proverPath, inputPath);
                    
                    if (jsonResult != null) {
                        return "Proof generated successfully for: " + selectedCircuit.getName() + "!\n\n" +
                               selectedCircuit.getDescription() + "\n\n" +
                               "JSON Output (first 500 chars):\n" + 
                               (jsonResult.length() > 500 ? jsonResult.substring(0, 500) + "..." : jsonResult);
                    } else {
                        // If JSON method fails, try file method
                        String outputPath = getFilesDir() + "/proof_output.np";
                        int result = ProveKitFFI.proveToFile(proverPath, inputPath, outputPath);
                        
                        if (result == ProveKitFFI.PK_SUCCESS) {
                            return "Proof generated successfully for: " + selectedCircuit.getName() + "!\n\n" +
                                   selectedCircuit.getDescription() + "\n\nProof saved to: " + outputPath;
                        } else {
                            return "Proof generation failed: " + ProveKitFFI.getErrorMessage(result);
                        }
                    }
                    
                } catch (Exception e) {
                    return "Exception during proof generation: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                textViewOutput.setText(result);
                updateGenerateButton();
                
                if (result.startsWith("Proof generated successfully")) {
                    Toast.makeText(MainActivity.this, "Proof generated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * Copy an asset file to the app's internal storage.
     * This is necessary because the native library needs file paths, not asset streams.
     * 
     * @param assetPath The full path of the file in the assets directory
     * @param fileName The output filename in internal storage
     * @return The absolute path of the copied file, or null on error
     */
    private String copyAssetToInternalStorage(String assetPath, String fileName) {
        try {
            InputStream inputStream = getAssets().open(assetPath);
            File outputFile = new File(getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}