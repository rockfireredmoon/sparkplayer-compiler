project('sparkplayer-compiler', ['cpp','c','java'], version: '1.0.0', default_options : [
	'cpp_std=c++14'])

# Whether to build Squirrel for 32 bit binary compatibility (when running on a 64 bit architecture)
if build_machine.cpu_family() == 'x86_64' and get_option('sq32')
	add_global_arguments(['-D_SQ32=1'],
	                   language: ['cpp','c'])
endif
if build_machine.system() == 'windows' and get_option('unicode')
	add_global_arguments([
			'-DUNICODE',
			'-D_UNICODE'
		],
		language: ['cpp','c'])
endif


# In-tree Dependencies
squirrel_dep = dependency('squirrel', fallback: ['squirrel','libsquirrel_dep'])
sqstdlib_dep = dependency('sqstdlib', fallback: ['squirrel','libsqstdlib_dep'])

javac = meson.get_compiler('java')
#ant_dep = javac.find_library('ant')
#ant_dep = dependency('ant')

# Executables
executable('car', 'src/main/c++/car/car.cpp', 
	install: true)
	
# Jar for Ant Tasks
subdir('src/main/java')
