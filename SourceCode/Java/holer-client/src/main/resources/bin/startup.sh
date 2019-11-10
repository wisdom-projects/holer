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
# Holer Startup
# -----------------------------------------------------------------------------
cd `dirname $0`/..

JAVA_BIN=$JAVA_HOME/bin/java

HOLER_OK=0
HOLER_ERR=1
HOLER_PID=""

HOLER_HOME=`pwd`
HOLER_LOG_DIR=$HOLER_HOME/logs
HOLER_LOG=$HOLER_LOG_DIR/holer-client.log
HOLER_APP=$HOLER_HOME/holer-client.jar
HOLER_CONF=$HOLER_HOME/conf/holer.conf

pid() 
{
    HOLER_PID=`ps -ef | grep -v grep | grep "$HOLER_APP" |awk '{print $2}'`
}

status() 
{
    pid
    if [ -n "$HOLER_PID" ]; then
        echo "Holer client PID <$HOLER_PID> is running."
    else
        echo "Holer client is stopped."
    fi
}

input() 
{
    if [ -f $HOLER_CONF ]; then
        . $HOLER_CONF
    fi

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

start() 
{
    if [ ! -d $HOLER_LOG_DIR ]; then
        mkdir -p $HOLER_LOG_DIR
    fi

    pid
    if [ -n "$HOLER_PID" ]; then
        status
        return $HOLER_OK
    fi

    if [ ! -f "$JAVA_BIN" ]; then
        JAVA_BIN=`which java`
        if [ ! -f "$JAVA_BIN" ]; then
            JAVA_BIN=java
        fi
    fi

    input
    nohup $JAVA_BIN -jar $HOLER_APP >> $HOLER_LOG 2>&1 &
    status
}

start
