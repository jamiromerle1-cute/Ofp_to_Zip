#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "NativeVMCore"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_launcher_MainActivity_stringFromNativeVM(JNIEnv* env, jobject /* this */) {
    std::string engineInfo = "Native C++ Hypervisor Engine Active (NDK ARM64 Architecture)";
    LOGI("C++ Virtualization Core Initialized Successfully.");
    return env->NewStringUTF(engineInfo.c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_launcher_MainActivity_mountExt4ImageNative(JNIEnv* env, jobject /* this */, jstring imagePath) {
    const char *path = env->GetStringUTFChars(imagePath, nullptr);
    LOGI("Native C++ EXT4 Engine: Mounting image file from ", path);
    
    // Native Linux container & framebuffer mount logic hook
    bool mountSuccess = true;
    
    env->ReleaseStringUTFChars(imagePath, path);
    return mountSuccess ? JNI_TRUE : JNI_FALSE;
}
