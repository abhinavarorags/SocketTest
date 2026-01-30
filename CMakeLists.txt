cmake_minimum_required(VERSION 3.10)
project(SocketLatency C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -D_GNU_SOURCE")

# Force CMake to treat .C files as C language
set_source_files_properties(randomData.C test.C test2.C PROPERTIES LANGUAGE C)

add_executable(randomData randomData.C)
add_executable(test test.C)
add_executable(test2 test2.C)
