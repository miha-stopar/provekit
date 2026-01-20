package com.example.provekitdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView textViewStatus;
    private TextView textViewOutput;
    private Button buttonGenerateProof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewOutput = findViewById(R.id.textViewOutput);
        buttonGenerateProof = findViewById(R.id.buttonGenerateProof);

        // Initialize ProveKit in background
        initializeProveKit();

        // Set button click listener
        buttonGenerateProof.setOnClickListener(v -> generateProof());
    }

    private void initializeProveKit() {
        textViewStatus.setText(getString(R.string.initializing));
        
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
                    buttonGenerateProof.setEnabled(true);
                } else if (result == -1) {
                    textViewStatus.setText("Error: Native library not found. Make sure libprovekit_ffi.so is included in APK.");
                    buttonGenerateProof.setEnabled(false);
                } else if (result == -2) {
                    textViewStatus.setText("Error: Exception during initialization.");
                    buttonGenerateProof.setEnabled(false);
                } else {
                    textViewStatus.setText("ProveKit initialization failed: " + ProveKitFFI.getErrorMessage(result));
                    buttonGenerateProof.setEnabled(false);
                }
            }
        }.execute();
    }

    private void generateProof() {
        buttonGenerateProof.setEnabled(false);
        textViewOutput.setText("Generating proof...");

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    // For this demo, we'll create dummy input files
                    // In a real app, you'd have actual prover and input files
                    
                    // Create dummy files in app's internal storage
                    String proverPath = createDummyProverFile();
                    String inputPath = createDummyInputFile();
                    
                    if (proverPath == null || inputPath == null) {
                        return "Error: Could not create test files";
                    }

                    // Try to generate proof as JSON
                    String jsonResult = ProveKitFFI.proveToJson(proverPath, inputPath);
                    
                    if (jsonResult != null) {
                        return "Proof generated successfully!\\n\\nJSON Output:\\n" + jsonResult;
                    } else {
                        // If JSON method fails, try file method
                        String outputPath = getFilesDir() + "/proof_output.np";
                        int result = ProveKitFFI.proveToFile(proverPath, inputPath, outputPath);
                        
                        if (result == ProveKitFFI.PK_SUCCESS) {
                            return "Proof generated successfully!\\nSaved to: " + outputPath;
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
                buttonGenerateProof.setEnabled(true);
                
                if (result.startsWith("Proof generated successfully")) {
                    Toast.makeText(MainActivity.this, "Proof generated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private String createDummyProverFile() {
        try {
            // Create a dummy prover file
            // In a real app, you'd copy actual prover files from assets or download them
            File proverFile = new File(getFilesDir(), "dummy_scheme.pkp");
            FileOutputStream fos = new FileOutputStream(proverFile);
            
            // This is just dummy data - replace with actual prover scheme
            byte[] dummyProverData = "DUMMY_PROVER_SCHEME_DATA".getBytes();
            fos.write(dummyProverData);
            fos.close();
            
            return proverFile.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    private String createDummyInputFile() {
        try {
            // Create a dummy input file in TOML format
            File inputFile = new File(getFilesDir(), "input.toml");
            FileOutputStream fos = new FileOutputStream(inputFile);
            
            // Example TOML input - replace with actual witness values
            String tomlContent = "# Example witness input\n" +
                "[main]\n" +
                "x = 10\n" +
                "y = 20\n" +
                "expected_result = 30\n";
            fos.write(tomlContent.getBytes());
            fos.close();
            
            return inputFile.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }
}