project('squirrel', ['cpp','c'], default_options : [
	'cpp_std=c++11'])
	
compiler = meson.get_compiler('cpp')

if host_machine.system() == 'windows'
	add_project_arguments(['/permissive'],
                     language: ['cpp','c'])
else
	add_project_arguments(['-fpermissive'],
                     language: ['cpp','c'])
endif
	

#Setup
squirrel_args = []
squirrel_inc = include_directories('include')
squirrel_lib_inc = include_directories('squirrel')

squirrel_lib = static_library('squirrel',[
	'squirrel/sqapi.cpp',
	'squirrel/sqbaselib.cpp',
	'squirrel/sqclass.cpp',
	'squirrel/sqcompiler.cpp',
	'squirrel/sqdebug.cpp',
	'squirrel/sqfuncstate.cpp',
	'squirrel/sqlexer.cpp',
	'squirrel/sqmem.cpp',
	'squirrel/sqobject.cpp',
	'squirrel/sqstate.cpp',
	'squirrel/sqtable.cpp',
	'squirrel/sqvm.cpp'], 
	cpp_args: squirrel_args,
	include_directories : squirrel_inc,
	install: true)
	
libsquirrel_dep = declare_dependency(include_directories : [squirrel_inc], link_with: squirrel_lib)
	
#Squirrel stdlib
sqstdlib_lib_inc = include_directories('sqstdlib')
sqstdlib_lib = static_library('sqstdlib',[
	'sqstdlib/sqstdaux.cpp',
	'sqstdlib/sqstdblob.cpp',
	'sqstdlib/sqstdio.cpp',
	'sqstdlib/sqstdmath.cpp',
	'sqstdlib/sqstdrex.cpp',
	'sqstdlib/sqstdstream.cpp',
	'sqstdlib/sqstdstring.cpp',
	'sqstdlib/sqstdsystem.cpp'], 
	include_directories : [
		squirrel_inc, 
		squirrel_lib_inc
	],
	cpp_args: squirrel_args,
	link_with: [squirrel_lib],
	install: true)
	
libsqstdlib_dep = declare_dependency(include_directories : [sqstdlib_lib_inc],link_with: sqstdlib_lib)
	
sq_exe = executable('eesq', 'sq/sq.c', 
	include_directories : [
		squirrel_inc, 
		squirrel_lib_inc,
		sqstdlib_lib_inc
	],
	c_args: squirrel_args,
	cpp_args: squirrel_args,
	link_with: [squirrel_lib,sqstdlib_lib], 
	install: true)
	
#Squirrel debug
sqdbg_inc = include_directories('include')
sqdbg_deps = []
if target_machine.system() == 'windows'
    sqdbg_deps += [
		compiler.find_library('ws2_32')
	]
endif

