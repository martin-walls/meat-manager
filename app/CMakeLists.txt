cmake_minimum_required(VERSION 3.4.1)

add_library( # name of the library
            native-lib

            # sets library as a shared library
            SHARED

            # provides a relative path to the source file
            src/main/cpp/native-lib.cpp )