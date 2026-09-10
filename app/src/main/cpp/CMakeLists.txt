cmake_minimum_required(VERSION 3.18.1)
project("vmnativeengine")

add_library(
        vmnativeengine
        SHARED
        native-core.cpp)

find_library(log-lib log)
find_library(android-lib android)
find_library(egl-lib EGL)
find_library(gles-lib GLESv2)

target_link_libraries(
        vmnativeengine
        ${log-lib}
        ${android-lib}
        ${egl-lib}
        ${gles-lib})
