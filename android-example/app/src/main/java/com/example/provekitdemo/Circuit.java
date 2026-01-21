package com.example.provekitdemo;

/**
 * Represents a zero-knowledge circuit that can be used for proof generation.
 */
public class Circuit {
    private String name;
    private String description;
    private String proverFile;
    private String inputFile;
    private String complexity;
    private String estimatedProvingTime;
    private String circuitPath;

    public Circuit(String circuitPath, String name, String description, String proverFile, String inputFile, 
                   String complexity, String estimatedProvingTime) {
        this.circuitPath = circuitPath;
        this.name = name;
        this.description = description;
        this.proverFile = proverFile;
        this.inputFile = inputFile;
        this.complexity = complexity;
        this.estimatedProvingTime = estimatedProvingTime;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getProverFile() { return proverFile; }
    public String getInputFile() { return inputFile; }
    public String getComplexity() { return complexity; }
    public String getEstimatedProvingTime() { return estimatedProvingTime; }
    public String getCircuitPath() { return circuitPath; }

    /**
     * Get the full path to the prover key file in assets.
     */
    public String getProverAssetPath() {
        return "circuits/" + circuitPath + "/" + proverFile;
    }

    /**
     * Get the full path to the input file in assets.
     */
    public String getInputAssetPath() {
        return "circuits/" + circuitPath + "/" + inputFile;
    }

    @Override
    public String toString() {
        return name + " (" + complexity + ")";
    }

    /**
     * Create a display string with full details for the circuit.
     */
    public String getDetailedDescription() {
        return name + "\n\n" + description + "\n\nComplexity: " + complexity + 
               "\nEstimated proving time: " + estimatedProvingTime;
    }
}