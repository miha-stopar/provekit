package com.example.provekitdemo;

import android.content.Context;
import android.content.res.AssetManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages available circuits and their metadata.
 */
public class CircuitManager {
    private Context context;
    private List<Circuit> availableCircuits;

    public CircuitManager(Context context) {
        this.context = context;
        this.availableCircuits = new ArrayList<>();
        loadCircuits();
    }

    /**
     * Load all available circuits from the assets/circuits directory.
     */
    private void loadCircuits() {
        try {
            AssetManager assetManager = context.getAssets();
            String[] circuitDirs = assetManager.list("circuits");
            
            if (circuitDirs != null) {
                for (String circuitDir : circuitDirs) {
                    try {
                        Circuit circuit = loadCircuit(circuitDir);
                        if (circuit != null) {
                            availableCircuits.add(circuit);
                        }
                    } catch (Exception e) {
                        // Skip circuits that fail to load
                        System.err.println("Failed to load circuit: " + circuitDir + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to list circuit directories: " + e.getMessage());
        }
    }

    /**
     * Load a single circuit from its directory.
     */
    private Circuit loadCircuit(String circuitDir) throws IOException, JSONException {
        AssetManager assetManager = context.getAssets();
        String metadataPath = "circuits/" + circuitDir + "/circuit.json";
        
        try (InputStream inputStream = assetManager.open(metadataPath)) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String json = new String(buffer);
            
            JSONObject metadata = new JSONObject(json);
            
            return new Circuit(
                circuitDir,
                metadata.getString("name"),
                metadata.getString("description"),
                metadata.getString("proverFile"),
                metadata.getString("inputFile"),
                metadata.getString("complexity"),
                metadata.getString("estimatedProvingTime")
            );
        }
    }

    /**
     * Get all available circuits.
     */
    public List<Circuit> getAvailableCircuits() {
        return new ArrayList<>(availableCircuits);
    }

    /**
     * Get a circuit by name.
     */
    public Circuit getCircuitByName(String name) {
        for (Circuit circuit : availableCircuits) {
            if (circuit.getName().equals(name)) {
                return circuit;
            }
        }
        return null;
    }

    /**
     * Get the number of available circuits.
     */
    public int getCircuitCount() {
        return availableCircuits.size();
    }
}