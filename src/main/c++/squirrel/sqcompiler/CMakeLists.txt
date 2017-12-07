add_executable (sq sq.c)
set_target_properties(sq PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
target_link_libraries( sq squirrel sqstdlib )