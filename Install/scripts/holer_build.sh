ARCH_ARM=arm
ARCH_ARMV7=${ARCH_ARM}v7
ARCH_PPC=ppc
ARCH_X86=x86
ARCH_MIPS=mips
ARCH_S390=s390

HOLER_ARCH=
HOLER_TYPE=

HOLER_NAME=holer
HOLER_FILES="$HOLER_NAME $HOLER_NAME.service $HOLER_NAME-uninstall.sh"
HOLER_LINUX=$HOLER_NAME-linux
HOLER_INSTALL=$HOLER_NAME-install

HOLER_PKG=$HOLER_LINUX.tar.gz
HOLER_ARM=$HOLER_LINUX-$ARCH_ARM
HOLER_PPC=$HOLER_LINUX-$ARCH_PPC
HOLER_X86=$HOLER_LINUX-$ARCH_X86
HOLER_MIPS=$HOLER_LINUX-$ARCH_MIPS
HOLER_S390=$HOLER_LINUX-$ARCH_S390

build() 
{
    HOLER_TYPE=$1
    HOLER_ARCH=$2

    tar czvf $HOLER_PKG $HOLER_TYPE $HOLER_FILES
    cat $HOLER_INSTALL.sh $HOLER_PKG > $HOLER_INSTALL.$HOLER_ARCH
    rm -f $HOLER_PKG
    echo ""
    echo "INSTALLER: $HOLER_INSTALL.$HOLER_ARCH"
    echo "-------------------------"
}

build_install() 
{
    # Build armv7
    build "${HOLER_ARM}v7*" $ARCH_ARMV7

    # Build arm
    build "$HOLER_ARM*" $ARCH_ARM

    # Build ppc
    build "$HOLER_PPC*" $ARCH_PPC

    # Build mips
    build "$HOLER_MIPS*" $ARCH_MIPS

    # Build x86
    build "$HOLER_X86* $HOLER_LINUX-i*" $ARCH_X86

    # Build s390
    build "$HOLER_S390*" $ARCH_S390

    # Build all
    build "$HOLER_LINUX-*" bin

    chmod +x $HOLER_INSTALL.*
}

build_install
