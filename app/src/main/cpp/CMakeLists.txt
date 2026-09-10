cmake_minimum_required(VERSION 3.18.1)
project("vmnativeengine")

add_library(
        vmnativeengine
        SHARED
        native-core.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        vmnativeengine
        ${log-lib})
