package com.example.provekitdemo;

/**
 * JNI wrapper for ProveKit FFI functions.
 * This class provides Java bindings for the native ProveKit library.
 */
public class ProveKitFFI {
    
    // Load the native library
    static {
        System.loadLibrary("provekit_jni");
    }

    // Error codes matching the C enum
    public static final int PK_SUCCESS = 0;
    public static final int PK_INVALID_INPUT = 1;
    public static final int PK_SCHEME_READ_ERROR = 2;
    public static final int PK_WITNESS_READ_ERROR = 3;
    public static final int PK_PROOF_ERROR = 4;
    public static final int PK_SERIALIZATION_ERROR = 5;
    public static final int PK_UTF8_ERROR = 6;
    public static final int PK_FILE_WRITE_ERROR = 7;

    /**
     * Initialize the ProveKit library.
     * Must be called once before using any other functions.
     * 
     * @return PK_SUCCESS on success, error code on failure
     */
    public static native int init();

    /**
     * Generate a proof and save it to a file.
     * 
     * @param proverPath Path to the prover scheme file (.pkp)
     * @param inputPath Path to the input/witness file (.toml)
     * @param outputPath Path where to save the proof file (.np or .json)
     * @return PK_SUCCESS on success, error code on failure
     */
    public static native int proveToFile(String proverPath, String inputPath, String outputPath);

    /**
     * Generate a proof and return it as a JSON string.
     * 
     * @param proverPath Path to the prover scheme file (.pkp)
     * @param inputPath Path to the input/witness file (.toml)
     * @return JSON string containing the proof, or null on error
     */
    public static native String proveToJson(String proverPath, String inputPath);

    /**
     * Get a human-readable error message for an error code.
     * 
     * @param errorCode The error code returned by other functions
     * @return Human-readable error message
     */
    public static String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case PK_SUCCESS:
                return "Success";
            case PK_INVALID_INPUT:
                return "Invalid input parameters";
            case PK_SCHEME_READ_ERROR:
                return "Failed to read scheme file";
            case PK_WITNESS_READ_ERROR:
                return "Failed to read witness/input file";
            case PK_PROOF_ERROR:
                return "Failed to generate proof";
            case PK_SERIALIZATION_ERROR:
                return "Failed to serialize output";
            case PK_UTF8_ERROR:
                return "UTF-8 conversion error";
            case PK_FILE_WRITE_ERROR:
                return "File write error";
            default:
                return "Unknown error: " + errorCode;
        }
    }
}