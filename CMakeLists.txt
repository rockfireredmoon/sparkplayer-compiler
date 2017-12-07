#----------------------------------------------------------------
# Native part of compiler tools
#
#----------------------------------------------------------------

#----------------------------------------------------------------
# Boilerplate
#----------------------------------------------------------------
cmake_minimum_required (VERSION 2.8.11)
project (taw-compiler)

#----------------------------------------------------------------
# CMake Configuration
#----------------------------------------------------------------
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_DEBUG_POSTFIX "_d")
set(CMAKE_INSTALL_PREFIX "${CMAKE_CURRENT_BINARY_DIR}/dist")

if (CMAKE_BUILD_TYPE STREQUAL "")
	# CMake defaults to leaving CMAKE_BUILD_TYPE empty. This screws up
	# differentiation between debug and release builds.
	set(CMAKE_BUILD_TYPE "RelWithDebInfo" CACHE STRING "Choose the type of build, options are: None (CMAKE_CXX_FLAGS or CMAKE_C_FLAGS used) Debug Release RelWithDebInfo MinSizeRel." FORCE)
endif ()

#set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/target/bin)

#----------------------------------------------------------------
# Subdirectories
#----------------------------------------------------------------
add_subdirectory (src)
