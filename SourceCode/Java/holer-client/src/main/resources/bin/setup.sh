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
cd `dirname $0`/..

HOLER_OK=0
HOLER_ERR=1
HOLER_NAME="holer"
HOLER_HOME=`pwd`
HOLER_BIN=$HOLER_HOME/bin
HOLER_CONF=$HOLER_HOME/conf/holer.conf
RC_INIT_DIR="/etc/rc.d/init.d"

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

init()
{
    input

    cp $HOLER_BIN/$HOLER_NAME $RC_INIT_DIR/
    sed -i "s|@HOLER_HOME@|$HOLER_HOME|" $RC_INIT_DIR/$HOLER_NAME

    chmod +x $RC_INIT_DIR/$HOLER_NAME
    chmod +x $HOLER_BIN/$HOLER_NAME*
    chmod +x $HOLER_BIN/*.sh
}

setup()
{
    init
    chkconfig --add $HOLER_NAME
    chkconfig $HOLER_NAME on
    chkconfig --list |grep $HOLER_NAME
}

setup
