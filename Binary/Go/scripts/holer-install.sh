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
# Holer Setup Script
# -----------------------------------------------------------------------------
cd `dirname $0`

HOLER_OK=0
HOLER_ERR=1

HOLER_HOME=`pwd`
HOLER_CONF=$HOLER_HOME/holer.conf
HOLER_PROGRAM=holer
HOLER_SERVICE=$HOLER_PROGRAM.service
HOLER_SHUTDOWN=$HOLER_HOME/shutdown.sh
HOLER_PKG_NAME=holer-linux-x86.tar.gz
HOLER_PKG_URL=https://raw.githubusercontent.com/wisdom-projects/holer/master/Binary/Go/$HOLER_PKG_NAME

USR_BIN_DIR="/usr/bin"
SYSD_DIR="/lib/systemd/system"
RCD_DIR="/etc/rc.d/init.d"

input() 
{
    # Asking for the holer access key
    if [ -z "$HOLER_ACCESS_KEY" ]; then
        echo "Enter holer access key:"
        read HOLER_ACCESS_KEY
        if [ -z "$HOLER_ACCESS_KEY" ]; then
            echo "Please enter holer access key."
            exit $HOLER_ERR
        fi
        echo "HOLER_ACCESS_KEY=$HOLER_ACCESS_KEY" > $HOLER_CONF
    fi

    # Asking for the holer server host
    if [ -z "$HOLER_SERVER_HOST" ]; then
        echo "Enter holer server host:"
        read HOLER_SERVER_HOST
        if [ -z "$HOLER_SERVER_HOST" ]; then
            echo "Please enter holer server host."
            exit $HOLER_ERR
        fi
        echo "HOLER_SERVER_HOST=$HOLER_SERVER_HOST" >> $HOLER_CONF
    fi
}

setup_sysd()
{
    if [ ! -d $SYSD_DIR ]; then
        return $HOLER_OK
    fi

    cp $HOLER_HOME/$HOLER_SERVICE $SYSD_DIR/
    sed -i "s|@HOLER_HOME@|$HOLER_HOME|" $SYSD_DIR/$HOLER_SERVICE
    chmod +x $HOLER_HOME/$HOLER_NAME*
    chmod +x $HOLER_HOME/*.sh

    systemctl enable $HOLER_SERVICE
    systemctl daemon-reload
    systemctl start $HOLER_SERVICE
    systemctl status $HOLER_SERVICE

    return $HOLER_OK
}

setup_rcd()
{
    if [ ! -d $RCD_DIR ]; then
        return $HOLER_OK
    fi

    cp $HOLER_HOME/$HOLER_PROGRAM $RCD_DIR/
    sed -i "s|@HOLER_HOME@|$HOLER_HOME|" $RCD_DIR/$HOLER_PROGRAM

    chmod +x $RCD_DIR/$HOLER_PROGRAM
    chmod +x $HOLER_HOME/$HOLER_PROGRAM*
    chmod +x $HOLER_HOME/*.sh

    chkconfig --add $HOLER_PROGRAM
    chkconfig $HOLER_PROGRAM on
    chkconfig --list |grep $HOLER_PROGRAM

    service $HOLER_PROGRAM start
    return $HOLER_OK
}

setup()
{
    $HOLER_SHUTDOWN > /dev/null 2>&1
    setup_sysd
    setup_rcd
}

download() 
{
    if [ -f $HOLER_HOME/$HOLER_PKG_NAME ]; then
        rm -f $HOLER_HOME/$HOLER_PKG_NAME
    fi
    wget $HOLER_PKG_URL
    tar -zxvf $HOLER_PKG_NAME
}

install()
{
    download
    input
    setup
}

install