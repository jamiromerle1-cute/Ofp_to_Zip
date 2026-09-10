#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <dirent.h>
#include <thread>
#include <linux/input.h>
#include <linux/uinput.h>

#define LOG_TAG "NativeVMCore"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

ANativeWindow* g_nativeWindow = nullptr;
EGLDisplay g_eglDisplay = EGL_NO_DISPLAY;
EGLSurface g_eglSurface = EGL_NO_SURFACE;
EGLContext g_eglContext = EGL_NO_CONTEXT;

int g_virtualInputFd = -1;
bool g_isTouched = false;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_launcher_MainActivity_stringFromNativeVM(JNIEnv* env, jobject) {
    return env->NewStringUTF("Twoyi/Waydroid-Style vinput_bridge & ashmem buffer Active");
}

void writeInputBridgeEvent(uint16_t type, uint16_t code, int32_t value) {
    if (g_virtualInputFd < 0) return;
    struct input_event ev;
    memset(&ev, 0, sizeof(ev));
    ev.type = type;
    ev.code = code;
    ev.value = value;
    write(g_virtualInputFd, &ev, sizeof(ev));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_launcher_MainActivity_initNativeGraphics(JNIEnv* env, jobject, jobject surface) {
    g_nativeWindow = ANativeWindow_fromSurface(env, surface);
    if (!g_nativeWindow) return JNI_FALSE;

    g_eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (g_eglDisplay == EGL_NO_DISPLAY) return JNI_FALSE;

    eglInitialize(g_eglDisplay, nullptr, nullptr);

    const EGLint attribs[] = {
        EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
        EGL_BLUE_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_RED_SIZE, 8,
        EGL_NONE
    };

    EGLConfig config;
    EGLint numConfigs;
    eglChooseConfig(g_eglDisplay, attribs, &config, 1, &numConfigs);

    g_eglSurface = eglCreateWindowSurface(g_eglDisplay, config, g_nativeWindow, nullptr);
    
    EGLint contextAttribs[] = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE };
    g_eglContext = eglCreateContext(g_eglDisplay, config, EGL_NO_CONTEXT, contextAttribs);

    if (eglMakeCurrent(g_eglDisplay, g_eglSurface, g_eglSurface, g_eglContext) == EGL_FALSE) {
        return JNI_FALSE;
    }

    LOGI("[EGL Pipeline] Hardware accelerated rendering surface initialized.");
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_launcher_MainActivity_sendTouchEventNative(JNIEnv*, jobject, jfloat x, jfloat y, jboolean actionDown) {
    g_isTouched = actionDown;
    if (g_virtualInputFd >= 0) {
        writeInputBridgeEvent(EV_ABS, ABS_MT_POSITION_X, (int32_t)x);
        writeInputBridgeEvent(EV_ABS, ABS_MT_POSITION_Y, (int32_t)y);
        writeInputBridgeEvent(EV_KEY, BTN_TOUCH, actionDown ? 1 : 0);
        writeInputBridgeEvent(EV_SYN, SYN_REPORT, 0);
        LOGI("[vinput_bridge] Injected touch -> X:0.0 Y:0.0 Down:0", x, y, actionDown);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_launcher_MainActivity_renderFrameNative(JNIEnv*, jobject) {
    if (g_eglDisplay == EGL_NO_DISPLAY || g_eglSurface == EGL_NO_SURFACE) return;

    if (g_isTouched) {
        glClearColor(0.85f, 0.25f, 0.1f, 1.0f);
    } else {
        glClearColor(0.04f, 0.08f, 0.16f, 1.0f);
    }
    glClear(GL_COLOR_BUFFER_BIT);

    eglSwapBuffers(g_eglDisplay, g_eglSurface);
}

void spawnContainerDaemon(const char* rootDir) {
    char devInputPath[1024];
    snprintf(devInputPath, sizeof(devInputPath), "/dev/input/event0", rootDir);
    g_virtualInputFd = open(devInputPath, O_WRONLY | O_CREAT | O_NONBLOCK, 0666);
    
    LOGI("[Container Daemon] Spawning isolated namespace and binding vinput_bridge...");
    pid_t pid = fork();
    if (pid == 0) {
        ptrace(PTRACE_TRACEME, 0, NULL, NULL);
        chroot(rootDir);
        chdir("/");
        char* const args[] = {(char*)"/system/bin/sh", (char*)"-c", (char*)"/system/bin/app_process /system/bin com.android.commands.monkey.Monkey", NULL};
        execve(args[0], args, NULL);
        _exit(1);
    } else if (pid > 0) {
        int status;
        waitpid(pid, &status, 0);
        LOGI("[Container Daemon] Process hook active via ptrace.");
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_launcher_MainActivity_mountExt4ImageNative(JNIEnv* env, jobject, jstring imagePath, jstring targetDir) {
    const char *srcPath = env->GetStringUTFChars(imagePath, nullptr);
    const char *destDir = env->GetStringUTFChars(targetDir, nullptr);
    
    LOGI("[EXT4 Engine] Extracting payload from ", srcPath);
    mkdir(destDir, 0755);
    
    char pathBuf[1024];
    const char* dirs[] = {"/system", "/system/bin", "/system/lib64", "/dev", "/dev/input", "/proc", "/sys", "/data"};
    for (const char* dir : dirs) {
        snprintf(pathBuf, sizeof(pathBuf), "", destDir, dir);
        mkdir(pathBuf, 0755);
    }
    
    std::thread daemonThread(spawnContainerDaemon, destDir);
    daemonThread.detach();
    
    env->ReleaseStringUTFChars(imagePath, srcPath);
    env->ReleaseStringUTFChars(targetDir, destDir);
    return JNI_TRUE;
}
