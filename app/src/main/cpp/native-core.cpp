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
    return env->NewStringUTF("DROID-FORMATTER Shizuku Partition Engine Active");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_launcher_MainActivity_formatPartitionNative(JNIEnv* env, jobject, jstring devicePath, jstring formatType) {
    const char *dev = env->GetStringUTFChars(devicePath, nullptr);
    const char *fmt = env->GetStringUTFChars(formatType, nullptr);
    
    char cmd[512];
    // Using Shizuku / adb shell execution context
    if (strcmp(fmt, "FAT32") == 0) {
        snprintf(cmd, sizeof(cmd), "sh -c \"mkfs.vfat -F 32 \"", dev);
    } else if (strcmp(fmt, "exFAT") == 0) {
        snprintf(cmd, sizeof(cmd), "sh -c \"mkfs.exfat \"", dev);
    } else if (strcmp(fmt, "EXT4") == 0) {
        snprintf(cmd, sizeof(cmd), "sh -c \"mkfs.ext4 -F \"", dev);
    } else if (strcmp(fmt, "NTFS") == 0) {
        snprintf(cmd, sizeof(cmd), "sh -c \"mkfs.ntfs -f \"", dev);
    } else if (strcmp(fmt, "Quick Wipe") == 0) {
        snprintf(cmd, sizeof(cmd), "sh -c \"dd if=/dev/zero of= bs=1M count=10\"", dev);
    } else {
        snprintf(cmd, sizeof(cmd), "sh -c \"badblocks -v \"", dev);
    }
    
    LOGI("[Shizuku Formatter] Executing adb/sh command: ", cmd);
    int result = system(cmd);
    
    env->ReleaseStringUTFChars(devicePath, dev);
    env->ReleaseStringUTFChars(formatType, fmt);
    
    return (result == 0) ? JNI_TRUE : JNI_FALSE;
}
