#include <jni.h>
#include <string>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <fcntl.h>

#define LOG_TAG "DroidFormatterCore"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_launcher_MainActivity_stringFromNativeVM(JNIEnv* env, jobject) {
    return env->NewStringUTF("DROID-FORMATTER Native Partition Engine Active");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_launcher_MainActivity_formatPartitionNative(JNIEnv* env, jobject, jstring devicePath, jstring formatType) {
    const char *dev = env->GetStringUTFChars(devicePath, nullptr);
    const char *fmt = env->GetStringUTFChars(formatType, nullptr);
    
    char cmd[512];
    if (strcmp(fmt, "FAT32") == 0) {
        snprintf(cmd, sizeof(cmd), "mkfs.vfat -F 32 ", dev);
    } else if (strcmp(fmt, "exFAT") == 0) {
        snprintf(cmd, sizeof(cmd), "mkfs.exfat ", dev);
    } else if (strcmp(fmt, "EXT4") == 0) {
        snprintf(cmd, sizeof(cmd), "mkfs.ext4 -F ", dev);
    } else if (strcmp(fmt, "NTFS") == 0) {
        snprintf(cmd, sizeof(cmd), "mkfs.ntfs -f ", dev);
    } else {
        LOGE("[Formatter] Unsupported format type.");
        env->ReleaseStringUTFChars(devicePath, dev);
        env->ReleaseStringUTFChars(formatType, fmt);
        return JNI_FALSE;
    }
    
    LOGI("[Formatter] Executing native command: ", cmd);
    int result = system(cmd);
    
    env->ReleaseStringUTFChars(devicePath, dev);
    env->ReleaseStringUTFChars(formatType, fmt);
    
    return (result == 0) ? JNI_TRUE : JNI_FALSE;
}
