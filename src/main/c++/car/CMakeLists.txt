add_executable (car car.cpp)
set_target_properties(car PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")