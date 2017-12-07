file(GLOB squirrel_SOURCES *.cpp *.h)

add_library (squirrel ${squirrel_SOURCES})
set_target_properties(squirrel PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")

