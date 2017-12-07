file(GLOB sqstdlib_SOURCES *.cpp *.h)

add_library (sqstdlib ${sqstdlib_SOURCES})
set_target_properties(sqstdlib PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
