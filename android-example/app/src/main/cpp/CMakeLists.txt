cmake_minimum_required(VERSION 3.22.1)

project("provekitdemo")

# Find required packages
find_library(log-lib log)

# Add our JNI wrapper
add_library(provekit_jni SHARED
    provekit_jni.cpp)

# Specify include directories
target_include_directories(provekit_jni PRIVATE
    ${CMAKE_SOURCE_DIR})

# Link with the prebuilt ProveKit FFI library
set(provekit_ffi_lib "${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libprovekit_ffi.so")

# Link libraries
target_link_libraries(provekit_jni
    ${log-lib}
    ${provekit_ffi_lib})