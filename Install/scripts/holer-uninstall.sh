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
# Holer Uninstallation Script
# -----------------------------------------------------------------------------
cd `dirname $0`

SYSD_DIR="/lib/systemd/system"
RCD_DIR="/etc/rc.d/init.d"
INITD_DIR="/etc/init.d"

HOLER_OK=0
HOLER_ERR=1

HOLER_LOG_DIR=/var/log
HOLER_BIN_DIR=/usr/bin
HOLER_CONF_DIR=/etc

HOLER_NAME=holer
HOLER_LOG_NAME=holer-client.log
HOLER_BIN_NAME=holer-linux-*
HOLER_CONF_NAME=holer.conf

HOLER_LOG=$HOLER_LOG_DIR/$HOLER_LOG_NAME
HOLER_BIN=$HOLER_BIN_DIR/$HOLER_BIN_NAME
HOLER_CONF=$HOLER_CONF_DIR/$HOLER_CONF_NAME
HOLER_PROGRAM=$HOLER_BIN_DIR/$HOLER_NAME
HOLER_SERVICE=$HOLER_NAME.service

unset_sysd()
{
    which systemctl >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        systemctl stop $HOLER_SERVICE
        systemctl disable $HOLER_SERVICE
        systemctl daemon-reload
    fi

    if [ -f $SYSD_DIR/$HOLER_SERVICE ]; then
        rm -f $SYSD_DIR/$HOLER_SERVICE
    fi

    return $HOLER_OK
}

unset_initd()
{
    which service >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        service $HOLER_NAME stop
    fi

    which chkconfig >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        chkconfig $HOLER_SERVICE off
        chkconfig --del $HOLER_SERVICE
    fi

    which update-rc.d >> $HOLER_LOG 2>&1
    if [ $? -eq 0 ]; then
        update-rc.d -f $HOLER_SERVICE remove
        update-rc.d -f $HOLER_NAME.sh remove
    fi

    if [ -f $RCD_DIR/$HOLER_SERVICE ]; then
        rm -f $RCD_DIR/$HOLER_SERVICE
    fi

    if [ -f $INITD_DIR/$HOLER_SERVICE ]; then
        rm -f $INITD_DIR/$HOLER_SERVICE
    fi

    if [ -f $INITD_DIR/$HOLER_NAME.sh ]; then
        rm -f $INITD_DIR/$HOLER_NAME.sh
    fi

    return $HOLER_OK
}

holer_unset()
{
    if [ -f $HOLER_PROGRAM ]; then
        sh $HOLER_PROGRAM stop
    fi
    unset_sysd >> $HOLER_LOG 2>&1
    unset_initd >> $HOLER_LOG 2>&1
}

holer_remove()
{
    rm -f $HOLER_BIN > /dev/null 2>&1
    if [ -f $HOLER_PROGRAM ]; then
        rm -f $HOLER_PROGRAM
    fi
    if [ -f $HOLER_CONF ]; then
        rm -f $HOLER_CONF
    fi
}

holer_uninstall()
{
    echo "Uninstalling holer..."
    holer_unset
    holer_remove
    echo "Done."
    exit $HOLER_OK
}

holer_uninstall
