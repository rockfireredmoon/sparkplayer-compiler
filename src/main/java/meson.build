# Ant jar directory (default path for web resources)
if get_option('antdir') == ''
	antdir = get_option('sysconfdir') + '/ant.d'
else
	antdir = get_option('antdir')
endif

# Jar for Ant Tasks
ant_jar = jar('sparkplayer-compiler', 
	'icemoon/compiler/FastMath.java',
	'icemoon/compiler/Abilities.java', 
	'icemoon/compiler/AbilityCompilerTask.java',
	'icemoon/compiler/AbstractBatchFileProcessor.java',
	'icemoon/compiler/AbstractCompilerTask.java',
	'icemoon/compiler/AbstractTool.java',
	'icemoon/compiler/AnimToXML.java',
	'icemoon/compiler/AssetDirCompileTask.java',
	'icemoon/compiler/Base.java',
	'icemoon/compiler/BaseCompilerTask.java',
	'icemoon/compiler/CAR.java',
	'icemoon/compiler/CARCompilerTask.java',
	'icemoon/compiler/CARDecode.java',
	'icemoon/compiler/CompilerUtil.java',
	'icemoon/compiler/DumbOutput.java',
	'icemoon/compiler/Fixer.java',
	'icemoon/compiler/LittleEndianDataInputStream.java',
	'icemoon/compiler/Matrix3f.java',
	'icemoon/compiler/Matrix4f.java',
	'icemoon/compiler/Mesh.java',
	'icemoon/compiler/MeshCompilerTask.java',
	'icemoon/compiler/MP3.java',
	'icemoon/compiler/MP3CompilerTask.java',
	'icemoon/compiler/NutCompilerTask.java',
	'icemoon/compiler/Output.java',
	'icemoon/compiler/Quaternion.java',
	'icemoon/compiler/Skeleton.java',
	'icemoon/compiler/SkeletonCompilerTask.java',
	'icemoon/compiler/Sq.java',
	'icemoon/compiler/SystemUtils.java',
	'icemoon/compiler/Vector2f.java',
	'icemoon/compiler/Vector3f.java',
	'icemoon/compiler/Vector4f.java',
	'icemoon/compiler/WAV.java',
	'icemoon/compiler/WAVCompilerTask.java',
	java_args: ['-classpath', meson.project_source_root() + '/lib/ant.jar'],
	install: true,
	install_dir: antdir)
