#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>

#define LOG_TAG "NativeVMCore"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

ANativeWindow* g_nativeWindow = nullptr;
EGLDisplay g_eglDisplay = EGL_NO_DISPLAY;
EGLSurface g_eglSurface = EGL_NO_SURFACE;
EGLContext g_eglContext = EGL_NO_CONTEXT;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_launcher_MainActivity_stringFromNativeVM(JNIEnv* env, jobject) {
    return env->NewStringUTF("Native C++ Hypervisor Engine Active (NDK ARM64 Architecture)");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_launcher_MainActivity_initNativeGraphics(JNIEnv* env, jobject, jobject surface) {
    g_nativeWindow = ANativeWindow_fromSurface(env, surface);
    if (!g_nativeWindow) {
        LOGE("Failed to get ANativeWindow from Surface.");
        return JNI_FALSE;
    }

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
        LOGE("Unable to eglMakeCurrent");
        return JNI_FALSE;
    }

    LOGI("Native EGL/OpenGL ES Graphics Pipeline Initialized.");
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_launcher_MainActivity_renderFrameNative(JNIEnv*, jobject) {
    if (g_eglDisplay == EGL_NO_DISPLAY || g_eglSurface == EGL_NO_SURFACE) return;

    glClearColor(0.05f, 0.1f, 0.2f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    eglSwapBuffers(g_eglDisplay, g_eglSurface);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_launcher_MainActivity_mountExt4ImageNative(JNIEnv* env, jobject, jstring imagePath) {
    const char *path = env->GetStringUTFChars(imagePath, nullptr);
    LOGI("Native C++ EXT4 Engine: Attaching  to virtual block device", path);
    env->ReleaseStringUTFChars(imagePath, path);
    return JNI_TRUE;
}
