#include <jni.h>
#include <android/log.h>
#include <string>

#define LOG_TAG "ProveKitFFI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Forward declarations for Rust FFI functions
extern "C" {
    int pk_init(void);
    int pk_prove_to_file(const char *prover_path, const char *input_path, const char *out_path);
    
    typedef struct {
        uint8_t *ptr;
        size_t len;
    } PKBuf;
    
    int pk_prove_to_json(const char *prover_path, const char *input_path, PKBuf *out_buf);
    void pk_free_buf(PKBuf buf);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_provekitdemo_ProveKitFFI_init(JNIEnv *env, jclass clazz) {
    LOGD("ProveKit FFI: init() called");
    int result = pk_init();
    LOGD("ProveKit FFI: init() result = %d", result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_provekitdemo_ProveKitFFI_proveToFile(JNIEnv *env, jclass clazz,
                                                       jstring prover_path, jstring input_path, jstring output_path) {
    LOGD("ProveKit FFI: proveToFile() called");
    
    // Convert Java strings to C strings
    const char *prover_cstr = env->GetStringUTFChars(prover_path, nullptr);
    const char *input_cstr = env->GetStringUTFChars(input_path, nullptr);
    const char *output_cstr = env->GetStringUTFChars(output_path, nullptr);
    
    if (!prover_cstr || !input_cstr || !output_cstr) {
        LOGE("ProveKit FFI: Failed to convert Java strings");
        return 1; // PK_INVALID_INPUT
    }
    
    LOGD("ProveKit FFI: prover_path=%s, input_path=%s, output_path=%s", 
         prover_cstr, input_cstr, output_cstr);
    
    // Call Rust function
    int result = pk_prove_to_file(prover_cstr, input_cstr, output_cstr);
    
    // Release string resources
    env->ReleaseStringUTFChars(prover_path, prover_cstr);
    env->ReleaseStringUTFChars(input_path, input_cstr);
    env->ReleaseStringUTFChars(output_path, output_cstr);
    
    LOGD("ProveKit FFI: proveToFile() result = %d", result);
    return result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_provekitdemo_ProveKitFFI_proveToJson(JNIEnv *env, jclass clazz,
                                                       jstring prover_path, jstring input_path) {
    LOGD("ProveKit FFI: proveToJson() called");
    
    // Convert Java strings to C strings
    const char *prover_cstr = env->GetStringUTFChars(prover_path, nullptr);
    const char *input_cstr = env->GetStringUTFChars(input_path, nullptr);
    
    if (!prover_cstr || !input_cstr) {
        LOGE("ProveKit FFI: Failed to convert Java strings");
        env->ReleaseStringUTFChars(prover_path, prover_cstr);
        env->ReleaseStringUTFChars(input_path, input_cstr);
        return nullptr;
    }
    
    LOGD("ProveKit FFI: prover_path=%s, input_path=%s", prover_cstr, input_cstr);
    
    // Call Rust function
    PKBuf buf;
    int result = pk_prove_to_json(prover_cstr, input_cstr, &buf);
    
    // Release string resources
    env->ReleaseStringUTFChars(prover_path, prover_cstr);
    env->ReleaseStringUTFChars(input_path, input_cstr);
    
    if (result != 0) {
        LOGE("ProveKit FFI: proveToJson() failed with result = %d", result);
        return nullptr;
    }
    
    // Convert result to Java string
    jstring json_result = env->NewStringUTF(reinterpret_cast<const char*>(buf.ptr));
    
    // Free the Rust-allocated buffer
    pk_free_buf(buf);
    
    LOGD("ProveKit FFI: proveToJson() success");
    return json_result;
}