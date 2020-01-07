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
# Holer Installation Script
# -----------------------------------------------------------------------------
cd `dirname $0`

SYSD_DIR="/lib/systemd/system"
RCD_DIR="/etc/rc.d/init.d"
INITD_DIR="/etc/init.d"

INSTALL_NAME=$0
INSTALL_OPTIONS="$@"
INSTALL_OPTION_NUM=$#

HOLER_OK=0
HOLER_ERR=1

HOLER_ARCH=`uname -m`
HOLER_CUR_DIR=`pwd`
HOLER_LOG_DIR=/var/log
HOLER_BIN_DIR=/usr/bin
HOLER_CONF_DIR=/etc

HOLER_NAME=holer
HOLER_LOG_NAME=holer-client.log
HOLER_LINUX=holer-linux
HOLER_CONF_NAME=holer.conf
HOLER_PKG_NAME=$HOLER_LINUX.tar.gz

HOLER_BIN_NAME=$HOLER_LINUX-$HOLER_ARCH
HOLER_LOG=$HOLER_LOG_DIR/$HOLER_LOG_NAME
HOLER_BIN=$HOLER_BIN_DIR/$HOLER_BIN_NAME
HOLER_CONF=$HOLER_CONF_DIR/$HOLER_CONF_NAME
HOLER_PROGRAM=$HOLER_BIN_DIR/$HOLER_NAME
HOLER_SERVICE=$HOLER_NAME.service

holer_input() 
{
    # Asking for the holer access key
    while [ -z "$HOLER_ACCESS_KEY" ]
    do
        echo "Enter holer access key:"
        read HOLER_ACCESS_KEY
    done
    echo "HOLER_ACCESS_KEY=$HOLER_ACCESS_KEY" > $HOLER_CONF

    # Asking for the holer server host
    while [ -z "$HOLER_SERVER_HOST" ]
    do
        echo "Enter holer server host:"
        read HOLER_SERVER_HOST
    done
    echo "HOLER_SERVER_HOST=$HOLER_SERVER_HOST" >> $HOLER_CONF
}

holer_bin()
{
    if [ $HOLER_ARCH = "i486" -o $HOLER_ARCH = "i586" -o $HOLER_ARCH = "i686" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-i386
    fi

    if [ $HOLER_ARCH = "armv5b" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-armv5l
    fi

    if [ $HOLER_ARCH = "armv6b" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-armv6l
    fi

    if [ $HOLER_ARCH = "armv7b" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-armv7l
    fi

    if [ $HOLER_ARCH = "armv8b" -o $HOLER_ARCH = "aarch64" -o $HOLER_ARCH = "aarch64_be" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-armv8l
    fi

    if [ $HOLER_ARCH = "s390" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-s390x
    fi

    if [ $HOLER_ARCH = "ppcle" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-ppc64le
    fi

    if [ $HOLER_ARCH = "ppc" ]; then
        HOLER_BIN_NAME=$HOLER_LINUX-ppc64
    fi
    HOLER_BIN=$HOLER_BIN_DIR/$HOLER_BIN_NAME
}

holer_init()
{
    if [ -f $HOLER_PROGRAM ]; then
        sh $HOLER_PROGRAM stop > /dev/null 2>&1
    fi

    tar -zxvf $HOLER_CUR_DIR/$HOLER_PKG_NAME >> $HOLER_LOG 2>&1

    chmod +x $HOLER_CUR_DIR/$HOLER_NAME
    chmod +x $HOLER_CUR_DIR/$HOLER_BIN_NAME

    cp $HOLER_CUR_DIR/$HOLER_NAME $HOLER_BIN_DIR/
    cp $HOLER_CUR_DIR/$HOLER_BIN_NAME $HOLER_BIN_DIR/

    chmod +x $HOLER_BIN
    chmod +x $HOLER_PROGRAM
}

setup_sysd()
{
    if [ -d $SYSD_DIR ]; then
        cp $HOLER_CUR_DIR/$HOLER_SERVICE $SYSD_DIR/
        chmod 644 $SYSD_DIR/$HOLER_SERVICE
    fi

    which systemctl >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        systemctl enable $HOLER_SERVICE
        systemctl daemon-reload
        systemctl start $HOLER_SERVICE
        systemctl status $HOLER_SERVICE
    fi

    return $HOLER_OK
}

setup_initd()
{
    if [ -d $RCD_DIR ]; then
        cp $HOLER_CUR_DIR/$HOLER_NAME $RCD_DIR/$HOLER_SERVICE
        chmod +x $RCD_DIR/$HOLER_SERVICE
    fi

    if [ -d $INITD_DIR ]; then
        cp $HOLER_CUR_DIR/$HOLER_NAME $INITD_DIR/$HOLER_SERVICE
        cp $HOLER_CUR_DIR/$HOLER_NAME $INITD_DIR/$HOLER_NAME.sh
        chmod +x $INITD_DIR/$HOLER_SERVICE
        chmod +x $INITD_DIR/$HOLER_NAME.sh
    fi

    which chkconfig >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        chkconfig --add $HOLER_SERVICE
        chkconfig $HOLER_SERVICE on
        chkconfig --list |grep $HOLER_SERVICE >> $HOLER_LOG 2>&1
    fi

    which update-rc.d >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        update-rc.d $HOLER_SERVICE defaults
        update-rc.d $HOLER_NAME.sh defaults
    fi

    which service >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        service $HOLER_NAME start >> $HOLER_LOG 2>&1
    fi

    return $HOLER_OK
}

holer_setup()
{
    setup_sysd >> $HOLER_LOG 2>&1
    setup_initd >> $HOLER_LOG 2>&1

    if [ -f $HOLER_PROGRAM ]; then
        sh $HOLER_PROGRAM start
    fi
}

holer_usage()
{

cat << HOLER_USAGE
    ************************************************************
    Usage   : 
    sh ${INSTALL_NAME} -k HOLER_ACCESS_KEY -s HOLER_SERVER_HOST
    ------------------------------------------------------------
    Example :
    sh ${INSTALL_NAME} -k a0b1c2d3e4f5g6h7i8j9k -s holer.org
    ************************************************************
HOLER_USAGE

}

holer_option()
{
    if [  $INSTALL_OPTION_NUM -lt 1 ]; then
        return $HOLER_OK
    fi

    while [ $# -ge 1 ]; do
        case $1 in
            -k )
                export HOLER_ACCESS_KEY=$2
                if [ $# -lt 2 ]; then 
                    break
                fi
                shift 2
                ;;
            -s )
                export HOLER_SERVER_HOST=$2
                if [ $# -lt 2 ]; then 
                    break
                fi
                shift 2
                ;;
            -h )
                holer_usage
                exit $HOLER_OK
                ;;
            * )
                if [ $# -lt 1 ]; then 
                    break
                fi
                shift
                ;;
        esac
    done
}

holer_install()
{
    HOLER_LINE_NUM=258
    tail -n +$HOLER_LINE_NUM $0 > $HOLER_CUR_DIR/$HOLER_PKG_NAME

    holer_option $INSTALL_OPTIONS
    holer_input

    echo "Installing holer..."

    holer_bin
    holer_init
    holer_setup

    echo "Done."
    exit $HOLER_OK
}

holer_install
