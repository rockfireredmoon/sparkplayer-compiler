# taw-compiler - Tools for compiling Earth Eternal 

Although a bit of a misnomer (as they are now used for compiling all editions of Earth Eternal that run on top
of *SparkPlayer*), these tools are used for compiling source assets (such as Squirrel Scripts, OGRE mesh
models etc) into binary files for distributing to clients (via an Earth Eternal server such as one of the IceEE impemenations, TAWD or VALD).

## Provided Tools

These tools all run via ANT, although some can be called directly if needed. See *sparkplayer-eartheternal* as
an example of how they are used.

  * Squirrel Compiler. Converts .nut into .cnut files
  * OGRE Mesh Converter. Converts OGRE XML mesh files into .mesh
  * Some tools to fix the mesh serializer version to make it compatible with existing Spark.exe clients
  * Ability Table compiler
  * MP3/WAV to OGG converter
  
## Building

To use the tools you will need to compile this module and add target/classes to the CLASSPATH of wherever
you want to use them from.

### Pre-requisites (build)

Make sure you have these installed and available on your PATH when you are building these tools.

  * Java development kit (Java 11 recommended)
  * Meson (tested with 0.50.0)
  * Ninja (tested with 1.8.2)
  * GCC (to compile native car and sq executables)
  
NOTE: All Earth Eternal tools are 32 bit to maintain compatibility. Ensure you have 32 bit standard C 
libraries installed.

### Pre-requisites (runtime)

Make sure you have these installed and available on your PATH when you are using the tools.

  * ffmpeg (for MP3 conversion)
  * oggeng (for WAV conversion)
  * OgreXmlConverter
  
NOTE: All Earth Eternal tools are 32 bit to maintain compatibility. Ensure you have 32 bit standard C 
libraries installed.

### Build

```
cd sparkplayer-compiler
meson default/build
cd default/build
ninja
```

### Install

You can install the tools using Meson.

```
sudo meson install
```

This will install `car` (or `car.exe`), `eesq` (or `eeq.exe`) to the systems global directory. The
Ant tasks will be installed as a `jar` to `[data-directory]/sparkplay-compiler.jar` that can be added to an ANT build file as a
taskdef to use the compiler tasks.
