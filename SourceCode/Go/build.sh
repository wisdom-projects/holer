#!/bin/bash

# Copyright 2018-present, Yudong (Dom) Wang
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -----------------------------------------------------------------------------
# Build script for Holer
# -----------------------------------------------------------------------------

HOLER_SUM="sha1sum"
HOLER_UPX=false
HOLER_GCFLAGS=""

HOLER_ARMS=(5 6 7)
HOLER_ARCHS=(amd64 386)

VERSION=`date -u +%Y%m%d`
HOLER_OSES=(windows linux darwin freebsd netbsd openbsd plan9 dragonfly solaris)
HOLER_LDFLAGS="-X main.VERSION=$VERSION -s -w"

function build_init()
{
    if ! hash sha1sum 2>/dev/null; then
        if ! hash shasum 2>/dev/null; then
            echo "Can not find 'sha1sum' or 'shasum'"
            echo "Please install one of them."
            exit
        fi
        HOLER_SUM="shasum"
    fi

    if hash upx 2>/dev/null; then
        HOLER_UPX=true
    fi
}

function build_run()
{
    HOLER_BIN=holer-${HOLER_OS}-${HOLER_ARCH}${HOLER_SUFFIX}
    
    if [ "$HOLER_ARCH" == "arm64" ]; then
        HOLER_BIN=holer-${HOLER_OS}-armv8
    fi
    
    env CGO_ENABLED=0 GOOS=$HOLER_OS GOARCH=$HOLER_ARCH GOARM=$HOLER_ARM go build -ldflags "$HOLER_LDFLAGS" -gcflags "$HOLER_GCFLAGS" -o ${HOLER_BIN} holer

    if $HOLER_UPX; then 
        upx -9 ${HOLER_BIN}
    fi
}

function build_linux()
{
    HOLER_OS=linux
    HOLER_ARCHS=(386 amd64 arm arm64 ppc64 ppc64le mips mipsle mips64 mips64le s390x)

    for HOLER_ARCH in ${HOLER_ARCHS[@]}; do
        if [ "$HOLER_ARCH" == "arm" ]; then
            # ARMv5 ARMv6 ARMv7
            for HOLER_ARM in ${HOLER_ARMS[@]}; do
                HOLER_SUFFIX=v$HOLER_ARM
                build_run
            done
        else
            HOLER_ARM=""
            HOLER_SUFFIX=""
            build_run
        fi
    done

    tar -zcf holer-${HOLER_OS}-x86.tar.gz holer-${HOLER_OS}-386 holer-${HOLER_OS}-amd64
    $HOLER_SUM holer-${HOLER_OS}-x86.tar.gz
    
    tar -zcf holer-${HOLER_OS}-arm.tar.gz holer-${HOLER_OS}-arm*
    $HOLER_SUM holer-${HOLER_OS}-arm.tar.gz
    
    tar -zcf holer-${HOLER_OS}-mips.tar.gz holer-${HOLER_OS}-mips*
    $HOLER_SUM holer-${HOLER_OS}-mips.tar.gz
    
    tar -zcf holer-${HOLER_OS}-ppc64.tar.gz holer-${HOLER_OS}-ppc64*
    $HOLER_SUM holer-${HOLER_OS}-ppc64.tar.gz
    
    tar -zcf holer-${HOLER_OS}-s390x.tar.gz holer-${HOLER_OS}-s390x
    $HOLER_SUM holer-${HOLER_OS}-s390x.tar.gz
}

function build_windows_darwin_plan9()
{
    HOLER_OSES=(windows darwin plan9)
    HOLER_ARCHS=(386 amd64)
    HOLER_ARM=""
    HOLER_SUFFIX=""

    for HOLER_OS in ${HOLER_OSES[@]}; do
        HOLER_SUFFIX=""
        if [ "$HOLER_OS" == "windows" ]; then
            HOLER_SUFFIX=".exe"
        fi
        for HOLER_ARCH in ${HOLER_ARCHS[@]}; do
            build_run
        done
        tar -zcf holer-${HOLER_OS}.tar.gz holer-${HOLER_OS}-*
        $HOLER_SUM holer-${HOLER_OS}.tar.gz
    done
}

function build_xbsd()
{
    HOLER_OSES=(freebsd netbsd openbsd)
    HOLER_ARCHS=(386 amd64 arm)
    HOLER_ARM=""
    HOLER_SUFFIX=""

    for HOLER_OS in ${HOLER_OSES[@]}; do
        for HOLER_ARCH in ${HOLER_ARCHS[@]}; do
            build_run
        done
        tar -zcf holer-${HOLER_OS}.tar.gz holer-${HOLER_OS}-*
        $HOLER_SUM holer-${HOLER_OS}.tar.gz
    done
}

function build_solaris_dragonfly()
{
    HOLER_OSES=(solaris dragonfly)
    HOLER_ARCH="amd64"
    HOLER_ARM=""
    HOLER_SUFFIX=""

    for HOLER_OS in ${HOLER_OSES[@]}; do
        build_run
        tar -zcf holer-${HOLER_OS}.tar.gz holer-${HOLER_OS}-*
        $HOLER_SUM holer-${HOLER_OS}.tar.gz
    done
}

function build_usage()
{
    cat << HOLER_BUILD_HELP

*********************************************

Usage: sh holerbuild.sh
       sh holerbuild -h
       sh holerbuild -c

---------------------------------------------

Without arguments the build will be started

-h  Display the usage of the build

-c  Clean the built files

*********************************************

HOLER_BUILD_HELP

    exit 0
}

function build_holer()
{
    if [ "$1" = "-h" ]; then
        build_usage
    fi

    if [ "$1" = "-c" ]; then
        rm -rf holer-*
        exit 0
    fi

    # Build Initialization
    build_init

    # Linux ==> (386 amd64 arm arm64 ppc64 ppc64le mips mipsle mips64 mips64le s390x)
    build_linux

    # (Windows Darwin Plan9) ==> (386 amd64)
    build_windows_darwin_plan9
        
    # (Freebsd Netbsd Openbsd) ==> (386 amd64 arm)
    build_xbsd

    # (Solaris Dragonfly) ==> amd64
    build_solaris_dragonfly
}

# Build Holer
build_holer $@