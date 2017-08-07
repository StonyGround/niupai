#!/bin/bash
export TMPDIR=/home/pc/tmpdir
NDK=/home/pc/android-ndk-r14b

ARM_PLATFORM=$NDK/platforms/android-19/arch-arm/
ARM_PREBUILT=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64

ARM64_PLATFORM=$NDK/platforms/android-21/arch-arm64/
ARM64_PREBUILT=$NDK/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64

X86_PLATFORM=$NDK/platforms/android-19/arch-x86/
X86_PREBUILT=$NDK/toolchains/x86-4.9/prebuilt/linux-x86_64

X86_64_PLATFORM=$NDK/platforms/android-21/arch-x86_64/
X86_64_PREBUILT=$NDK/toolchains/x86_64-4.9/prebuilt/linux-x86_64

MIPS_PLATFORM=$NDK/platforms/android-19/arch-mips/
MIPS_PREBUILT=$NDK/toolchains/mipsel-linux-android-4.9/prebuilt/linux-x86_64

ADDI_CFLAGS="-I$(pwd)/x264/android/include/"
ADDI_LDFLAGS="-L$(pwd)/x264/android/lib/"
function build_one
{
if [ $ARCH == "arm" ]
then
    PLATFORM=$ARM_PLATFORM
    PREBUILT=$ARM_PREBUILT
    HOST=arm-linux-androideabi
#added by alexvas
elif [ $ARCH == "arm64" ]
then
    PLATFORM=$ARM64_PLATFORM
    PREBUILT=$ARM64_PREBUILT
    HOST=aarch64-linux-android
elif [ $ARCH == "mips" ]
then
    PLATFORM=$MIPS_PLATFORM
    PREBUILT=$MIPS_PREBUILT
    HOST=mipsel-linux-android
#alexvas
elif [ $ARCH == "x86_64" ]
then
    PLATFORM=$X86_64_PLATFORM
    PREBUILT=$X86_64_PREBUILT
    HOST=x86_64-linux-android
else
    PLATFORM=$X86_PLATFORM
    PREBUILT=$X86_PREBUILT
    HOST=i686-linux-android
fi

./configure \
    --prefix=$PREFIX \
    --enable-shared \
    --disable-static \
    --enable-nonfree \
    --enable-gpl \
    --enable-asm \
    --enable-small \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-ffserver \
    --disable-avdevice \
    --disable-symver \
    --enable-libx264 \
    --enable-encoder=libx264 \
    --enable-decoder=h264 \
    --enable-protocol=rtp \
    --enable-zlib \
    --cross-prefix=$PREBUILT/bin/$HOST- \
    --target-os=linux \
    --arch=$ARCH \
    --enable-cross-compile \
    --sysroot=$PLATFORM \
    --extra-cflags="-fvisibility=hidden -fdata-sections -ffunction-sections -Os -fPIC -DANDROID -DHAVE_SYS_UIO_H=1 -Dipv6mr_interface=ipv6mr_ifindex -fasm -Wno-psabi -fno-short-enums -fno-strict-aliasing -finline-limit=300 $ADDI_CFLAGS" \
    --extra-ldflags="-Wl,-rpath-link=$PLATFORM/usr/lib -L$PLATFORM/usr/lib -nostdlib -lc -lm -ldl -llog $ADDI_LDFLAGS" \
$ADDITIONAL_CONFIGURE_FLAG
make clean
make -j4
make install V=1
}
#arm v5te
#CPU=armv5te
#ARCH=arm
#OPTIMIZE_CFLAGS="-marm -march=$CPU"
#PREFIX=$BUILD_DIR/$CPU
#ADDITIONAL_CONFIGURE_FLAG=
#build_one

#arm v6
#CPU=armv6
#ARCH=arm
#OPTIMIZE_CFLAGS="-marm -march=$CPU"
#PREFIX=`pwd`/ffmpeg-android/$CPU 
#ADDITIONAL_CONFIGURE_FLAG=
#build_one

#arm v7vfpv3
#CPU=armv7-a
#ARCH=arm
#OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfpv3-d16 -marm -march=$CPU "
#PREFIX=$BUILD_DIR/$CPU
#ADDITIONAL_CONFIGURE_FLAG=
#build_one

#arm v7vfp
#CPU=armv7-a
#ARCH=arm
#PREFIX=./android
#ADDITIONAL_CONFIGURE_FLAG=
#build_one

#arm v7n
#CPU=armv7-a
#ARCH=arm
#OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=neon -marm -march=$CPU -mtune=cortex-a8"
#PREFIX=`pwd`/ffmpeg-android/$CPU
#ADDITIONAL_CONFIGURE_FLAG=--enable-neon
#build_one

#arm v6+vfp
#CPU=armv6
#ARCH=arm
#OPTIMIZE_CFLAGS="-DCMP_HAVE_VFP -mfloat-abi=softfp -mfpu=vfp -marm -march=$CPU"
#PREFIX=`pwd`/ffmpeg-android/${CPU}_vfp
#ADDITIONAL_CONFIGURE_FLAG=
#build_one

#arm64-v8a
CPU=arm64-v8a
ARCH=arm64
OPTIMIZE_CFLAGS=
PREFIX=./android
ADDITIONAL_CONFIGURE_FLAG=
build_one

#x86_64
#CPU=x86_64
#ARCH=x86_64
#OPTIMIZE_CFLAGS="-fomit-frame-pointer"
#PREFIX=$BUILD_DIR/$CPU
#ADDITIONAL_CONFIGURE_FLAG=
#build_one

#x86
#CPU=i686
#ARCH=i686
#OPTIMIZE_CFLAGS="-fomit-frame-pointer"
#PREFIX=$BUILD_DIR/$CPU
#ADDITIONAL_CONFIGURE_FLAG=
#build_one
